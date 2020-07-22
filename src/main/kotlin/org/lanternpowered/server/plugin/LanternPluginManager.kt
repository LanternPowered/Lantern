/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.plugin

import org.apache.logging.log4j.Logger
import org.lanternpowered.api.Game
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.withCause
import org.lanternpowered.api.plugin.PluginContainer
import org.lanternpowered.api.plugin.PluginManager
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.api.util.collections.getAndRemoveAll
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.api.util.optional.orNull
import org.lanternpowered.launch.LanternClassLoader
import org.lanternpowered.server.event.LanternEventManager
import org.lanternpowered.server.event.lifecycle.LanternConstructPluginEvent
import org.spongepowered.plugin.PluginCandidate
import org.spongepowered.plugin.PluginEnvironment
import org.spongepowered.plugin.PluginKeys
import org.spongepowered.plugin.PluginLanguageService
import org.spongepowered.plugin.metadata.PluginMetadata
import java.nio.file.Path
import java.util.Optional
import java.util.ServiceConfigurationError
import java.util.ServiceLoader

class LanternPluginManager(
        private val game: Game,
        private val logger: Logger,
        private val eventManager: LanternEventManager,
        private val baseDirectory: Path,
        private val pluginsDirectory: Path
) : PluginManager {

    companion object {

        const val LANTERN_ID = "lantern"
        const val SPONGE_API_ID = "spongeapi"
        const val SPONGE_ID = "sponge"
        const val MINECRAFT_ID = "minecraft"
    }

    private val byId = mutableMapOf<String, PluginContainer>()
    private val byInstance = mutableMapOf<Any, PluginContainer>()

    lateinit var lanternPlugin: PluginContainer
        private set

    lateinit var spongeApiPlugin: PluginContainer
        private set

    lateinit var spongePlugin: PluginContainer
        private set

    lateinit var minecraftPlugin: PluginContainer
        private set

    fun instantiate() {
        val languageServices = findLanguageServices()
        instantiate(languageServices)
    }

    private fun findLanguageServices(): List<PluginLanguageService<out PluginContainer>> {
        val services = mutableListOf<PluginLanguageService<out PluginContainer>>()

        val it = ServiceLoader.load(PluginLanguageService::class.java).iterator()
        while (it.hasNext()) {
            try {
                services += it.next()
            } catch (ex: ServiceConfigurationError) {
                this.logger.error("Error encountered initializing plugin language service!", ex)
                continue
            }
        }

        return services
    }

    private fun instantiate(languageServices: List<PluginLanguageService<out PluginContainer>>) {
        val classLoader = LanternClassLoader.get()

        val environment = PluginEnvironment()
        environment.blackboard.getOrCreate(PluginKeys.BASE_DIRECTORY) { this.baseDirectory }
        environment.blackboard.getOrCreate(PluginKeys.PLUGIN_DIRECTORIES) { listOf(this.pluginsDirectory) }
        // TODO: Add parent injector

        val resources = mutableSetOf<Path>()
        val candidatesByService = mutableListOf<Pair<PluginLanguageService<out PluginContainer>, PluginCandidate>>()
        for (service in languageServices) {
            try {
                resources += service.discoverPluginResources(environment)
            } catch (t: Throwable) {
                this.logger.error("The plugin language service ${service.name} " +
                        "(${service.javaClass.name}) failed to discover plugin resources.", t)
                continue
            }

            val candidates = try {
                service.createPluginCandidates(environment)
            } catch (t: Throwable) {
                this.logger.error("The plugin language service ${service.name} " +
                        "(${service.javaClass.name}) failed to create plugin candidates.", t)
                continue
            }

            for (candidate in candidates)
                candidatesByService += service to candidate
        }

        // Load all the plugin jars and directories
        // TODO: Only add jars/directories that aren't on the classpath
        for (path in resources)
            classLoader.addBaseURL(path.toUri().toURL())

        fun extractInternalCandidate(id: String): PluginContainer {
            val candidate = candidatesByService
                    .getAndRemoveAll { (_, candidate) -> candidate.metadata.id == id }
                    .firstOrNull()?.second
            val metadata = candidate?.metadata ?: PluginMetadata.builder().setId(id).build()
            val file = candidate?.file ?: this.baseDirectory.resolve("magic/$id.jar") // Use something as path.

            val pluginContainer = InternalPluginContainer(file, metadata, this.logger, this.game)
            this.byId[id] = pluginContainer
            return pluginContainer
        }

        // These are internal plugins and shouldn't be constructed through the service

        this.lanternPlugin = extractInternalCandidate(LANTERN_ID)
        this.spongeApiPlugin = extractInternalCandidate(SPONGE_API_ID)
        this.spongePlugin = extractInternalCandidate(SPONGE_ID)
        this.minecraftPlugin = extractInternalCandidate(MINECRAFT_ID)

        // Load other plugin instances

        val causeStack = CauseStack.current()

        for ((service, candidate) in candidatesByService) {
            val pluginContainer = try {
                service.createPluginContainer(candidate, environment).orNull() ?: continue
            } catch (t: Throwable) {
                this.logger.error("Failed to create the plugin plugin of ${candidate.asString()}", t)
                continue
            }

            // Already store it so it can be looked up through injections.
            this.byId[candidate.metadata.id] = pluginContainer

            // Already put the plugin container in the cause stack so
            // it's available during injections, etc.
            causeStack.withCause(pluginContainer) {
                // Instantiate the plugin instance.
                try {
                    @Suppress("UNCHECKED_CAST")
                    (service as PluginLanguageService<PluginContainer>).loadPlugin(environment, pluginContainer, classLoader)
                    this.byInstance[pluginContainer.instance] = pluginContainer
                } catch (t: Throwable) {
                    this.logger.error("Failed to instantiate the plugin instance of ${candidate.asString()}", t)
                    // Remove the failed plugin
                    this.byId -= candidate.metadata.id
                }
            }
        }
    }

    private fun PluginCandidate.asString(): String = ToStringHelper(this)
            .add("file", this.file)
            .add("metadata", this.metadata)
            .toString()

    /**
     * Registers all the listeners of the plugins and
     * calls their construction events.
     */
    fun construct() {
        // Register the listeners of all the plugins first, so they can
        // already receive events posted by other plugins.
        for (plugin in this.byInstance.values)
            this.eventManager.registerListeners(plugin, plugin.instance)

        for (plugin in this.byInstance.values)
            this.eventManager.postFor(LanternConstructPluginEvent(this.game, plugin), plugin)
    }

    override fun isLoaded(id: String): Boolean =
            this.byId.containsKey(id)

    override fun getPlugin(id: String): Optional<PluginContainer> =
            this.byId[id].optional()

    override fun getPlugins(): Collection<PluginContainer> =
            this.byId.values.toImmutableList()

    override fun fromInstance(instance: Any): Optional<PluginContainer> =
            if (instance is PluginContainer) instance.optional() else this.byInstance[instance].optional()
}
