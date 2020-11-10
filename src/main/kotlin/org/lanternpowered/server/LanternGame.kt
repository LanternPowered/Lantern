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
package org.lanternpowered.server

import com.google.inject.Guice
import joptsimple.OptionSet
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.lanternpowered.api.Game
import org.lanternpowered.api.PlatformComponent
import org.lanternpowered.api.Sponge
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.injector.Injector
import org.lanternpowered.api.plugin.PluginContainer
import org.lanternpowered.api.plugin.PluginManager
import org.lanternpowered.api.registry.GameRegistry
import org.lanternpowered.api.util.palette.PaletteBasedArrayFactory
import org.lanternpowered.server.config.GlobalConfigObject
import org.lanternpowered.server.config.LanternConfigManager
import org.lanternpowered.server.console.LanternConsole
import org.lanternpowered.server.event.LanternEventManager
import org.lanternpowered.server.event.lifecycle.LanternLoadedGameEvent
import org.lanternpowered.server.game.LanternPlatform
import org.lanternpowered.server.game.version.LanternMinecraftVersion
import org.lanternpowered.server.game.version.MinecraftVersionCache
import org.lanternpowered.server.network.protocol.Protocol
import org.lanternpowered.server.permission.Permissions
import org.lanternpowered.server.plugin.LanternPluginManager
import org.lanternpowered.server.registry.LanternGameRegistry
import org.lanternpowered.server.scheduler.LanternScheduler
import org.lanternpowered.server.service.LanternServiceProvider
import org.lanternpowered.server.service.context.LanternContextCalculator
import org.lanternpowered.server.service.pagination.LanternPaginationService
import org.lanternpowered.server.service.permission.LanternPermissionService
import org.lanternpowered.server.sql.LanternSqlManager
import org.lanternpowered.server.text.TranslationRegistries
import org.lanternpowered.server.text.Translators
import org.lanternpowered.server.util.LocaleCache
import org.lanternpowered.server.util.guice.GuiceModule
import org.lanternpowered.server.util.metric.LanternMetricsConfigManager
import org.lanternpowered.server.util.palette.LanternPaletteBasedArrayFactory
import org.spongepowered.api.SystemSubject
import org.spongepowered.api.asset.AssetManager
import org.spongepowered.api.command.manager.CommandManager
import org.spongepowered.api.config.ConfigManager
import org.spongepowered.api.data.DataManager
import org.spongepowered.api.network.channel.ChannelRegistry
import org.spongepowered.api.service.ban.BanService
import org.spongepowered.api.service.context.Contextual
import org.spongepowered.api.service.context.ContextualService
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.service.pagination.PaginationService
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.service.permission.SubjectData
import org.spongepowered.api.service.whitelist.WhitelistService
import org.spongepowered.api.sql.SqlManager
import org.spongepowered.api.util.Tristate
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Locale

object LanternGame : Game {

    /**
     * The number of ticks per second in vanilla minecraft.
     */
    const val MINECRAFT_TICKS_PER_SECOND = 20

    /**
     * The number of milliseconds per tick in vanilla minecraft.
     */
    private const val MINECRAFT_TICK_MILLIS = 1000 / MINECRAFT_TICKS_PER_SECOND

    /**
     * Gets the current time in ticks. This method is similar to
     * [System.currentTimeMillis] but the unit is converted
     * to ticks.
     *
     * @return The current time in ticks
     */
    @Deprecated(message = "Avoid using ticks.")
    fun currentTimeTicks(): Long = System.currentTimeMillis() / MINECRAFT_TICK_MILLIS

    /**
     * The current protocol version number that's supported.
     */
    const val PROTOCOL_VERSION = 714

    /**
     * The logger.
     */
    val logger: Logger = LogManager.getLogger("lantern")

    private lateinit var platform: LanternPlatform
    private lateinit var pluginManager: LanternPluginManager
    private lateinit var registry: LanternGameRegistry
    private lateinit var eventManager: LanternEventManager
    private lateinit var sqlManager: LanternSqlManager
    private lateinit var serviceProvider: LanternServiceProvider
    private lateinit var configManager: LanternConfigManager
    private lateinit var server: LanternServer
    private lateinit var metricsConfigManager: LanternMetricsConfigManager
    private lateinit var gameDirectory: Path
    private lateinit var console: LanternConsole
    private lateinit var asyncScheduler: LanternScheduler

    /**
     * The current minecraft version.
     */
    lateinit var minecraftVersion: LanternMinecraftVersion
        private set

    lateinit var minecraftVersionCache: MinecraftVersionCache
        private set

    /**
     * The config directory.
     */
    lateinit var configDirectory: Path
        private set

    val minecraftPlugin: PluginContainer
        get() = this.pluginManager.minecraftPlugin

    val lanternPlugin: PluginContainer
        get() = this.pluginManager.lanternPlugin

    val spongeApiPlugin: PluginContainer
        get() = this.pluginManager.spongeApiPlugin

    val spongePlugin: PluginContainer
        get() = this.pluginManager.spongePlugin

    val config: GlobalConfigObject = TODO()

    override val paletteBasedArrayFactory: PaletteBasedArrayFactory
        get() = LanternPaletteBasedArrayFactory

    override val injector: Injector
        get() = TODO("Not yet implemented")

    fun init(options: OptionSet, console: LanternConsole, asyncScheduler: LanternScheduler) {
        injectSpongeGame()

        this.asyncScheduler = asyncScheduler
        this.console = console

        this.gameDirectory = Paths.get("")
        this.configDirectory = this.gameDirectory.resolve(options.valueOf(LaunchOptions.CONFIG_DIRECTORY) ?: "config")

        // TODO: Load config

        this.eventManager = LanternEventManager
        this.configManager = LanternConfigManager(this.logger, this.configDirectory)
        this.sqlManager = LanternSqlManager(this.configManager)
        this.metricsConfigManager = LanternMetricsConfigManager(this.config)

        val contextCalculator = LanternContextCalculator<Contextual>(this)
        this.serviceProvider = LanternServiceProvider(this)
        this.serviceProvider.onRegister { registration ->
            val service = registration.service()
            @Suppress("UNCHECKED_CAST")
            (service as? ContextualService<Contextual>)?.registerContextCalculator(contextCalculator)
        }

        val pluginsDirectory = this.gameDirectory.resolve(options.valueOf(LaunchOptions.PLUGINS_DIRECTORY) ?: "plugins")

        // Construct all the plugins
        this.pluginManager = LanternPluginManager(this, this.logger, this.eventManager, this.gameDirectory, pluginsDirectory)
        this.pluginManager.findCandidates()

        this.minecraftVersionCache = MinecraftVersionCache()
        this.minecraftVersionCache.init()

        this.minecraftVersion = LanternMinecraftVersion(
                this.minecraftPlugin.metadata.version, PROTOCOL_VERSION, false)

        val versionCacheEntry = this.minecraftVersionCache.getVersionOrUnknown(PROTOCOL_VERSION, false)
        check(versionCacheEntry == this.minecraftVersion) {
            "The current version and version in the cache don't match: $minecraftVersion != $versionCacheEntry" }

        this.platform = LanternPlatform(mapOf(
                PlatformComponent.API to this.spongeApiPlugin,
                PlatformComponent.GAME to this.minecraftPlugin,
                PlatformComponent.IMPLEMENTATION to this.lanternPlugin
        ), this.minecraftVersion)

        // Instantiate plugins and call construct lifecycle events
        this.pluginManager.instantiate()
        this.pluginManager.construct()

        // Register all factories, builders and catalog registries
        this.registry = LanternGameRegistry(this)
        this.registry.init()

        // Load the translation files
        TranslationRegistries.init()
        Translators.init()

        this.initPermissionService()
        this.initPaginationService()

        // Initialize the console subject
        this.console.resolveSubject()

        this.serviceProvider.register<EconomyService>()
        this.serviceProvider.register<WhitelistService>() // TODO
        this.serviceProvider.register<BanService>() // TODO
        this.serviceProvider.register<PaginationService>() // TODO

        // And... done
        this.eventManager.post(LanternLoadedGameEvent(this))
    }

    /**
     * Inject this [Game] instance into the [Sponge] class.
     */
    private fun injectSpongeGame() {
        Guice.createInjector(object : GuiceModule() {
            override fun configure() {
                bind<org.spongepowered.api.Game>().toInstance(this@LanternGame)
                requestStaticInjection(Sponge::class.java)
            }
        })
    }

    /**
     * Sets the server instance.
     */
    fun setServer(server: LanternServer) { this.server = server }

    /**
     * Initializes the permission service.
     */
    private fun initPermissionService() {
        val service = this.serviceProvider.register<PermissionService> {
            this.lanternPlugin to LanternPermissionService()
        }
        if (service is LanternPermissionService) {
            fun applyDefault(opLevel: Int, permission: String, state: Tristate = Tristate.TRUE) = service
                    .getGroupForOpLevel(opLevel).subjectData.setPermission(SubjectData.GLOBAL_CONTEXT, permission, state)

            applyDefault(Permissions.SELECTOR_LEVEL, Permissions.SELECTOR_PERMISSION)
            applyDefault(Permissions.COMMAND_BLOCK_LEVEL, Permissions.COMMAND_BLOCK_PERMISSION)
            applyDefault(Permissions.Login.BYPASS_PLAYER_LIMIT_LEVEL, Permissions.Login.BYPASS_PLAYER_LIMIT_PERMISSION, Tristate.FALSE)
            applyDefault(Permissions.Login.BYPASS_WHITELIST_LEVEL, Permissions.Login.BYPASS_WHITELIST_PERMISSION)
            applyDefault(Permissions.Chat.FORMAT_URLS_LEVEL, Permissions.Chat.FORMAT_URLS)
            applyDefault(Permissions.Chat.BYPASS_SPAM_CHECK_LEVEL, Permissions.Chat.BYPASS_SPAM_CHECK)
        }
    }

    /**
     * Initializes the pagination service.
     */
    private fun initPaginationService() {
        val service = this.serviceProvider.register<PaginationService> {
            this.lanternPlugin to LanternPaginationService()
        }
        if (service is LanternPaginationService) {
            // TODO: Register pagination command in the command manager
        }
    }

    override fun getMetricsConfigManager(): LanternMetricsConfigManager = this.metricsConfigManager
    override fun getServer(): LanternServer = this.server
    override fun getRegistry(): GameRegistry = this.registry
    override fun getPluginManager(): PluginManager = this.pluginManager
    override fun getPlatform(): LanternPlatform = this.platform
    override fun getSqlManager(): SqlManager = this.sqlManager
    override fun getEventManager(): EventManager = this.eventManager
    override fun getConfigManager(): ConfigManager = this.configManager
    override fun getServiceProvider(): LanternServiceProvider = this.serviceProvider
    override fun getGameDirectory(): Path = this.gameDirectory
    override fun getSystemSubject(): SystemSubject = this.console
    override fun getAsyncScheduler(): LanternScheduler = this.asyncScheduler

    override fun getLocale(locale: String): Locale = LocaleCache[locale]

    override fun getCommandManager(): CommandManager {
        TODO("Not yet implemented")
    }

    override fun getAssetManager(): AssetManager {
        TODO("Not yet implemented")
    }

    override fun getDataManager(): DataManager {
        TODO("Not yet implemented")
    }

    override fun getChannelRegistry(): ChannelRegistry {
        TODO("Not yet implemented")
    }

    override fun isServerAvailable(): Boolean = this::server.isInitialized
}
