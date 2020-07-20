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

import joptsimple.OptionSet
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.lanternpowered.api.Game
import org.lanternpowered.api.MinecraftVersion
import org.lanternpowered.api.Platform
import org.lanternpowered.api.PlatformComponent
import org.lanternpowered.api.Server
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.injector.Injector
import org.lanternpowered.api.plugin.PluginContainer
import org.lanternpowered.api.plugin.PluginManager
import org.lanternpowered.api.registry.GameRegistry
import org.lanternpowered.api.util.palette.PaletteBasedArrayFactory
import org.lanternpowered.server.config.GlobalConfigObject
import org.lanternpowered.server.config.LanternConfigManager
import org.lanternpowered.server.event.LanternEventManager
import org.lanternpowered.server.event.lifecycle.LanternLoadedGameEvent
import org.lanternpowered.server.game.LanternPlatform
import org.lanternpowered.server.game.version.LanternMinecraftVersion
import org.lanternpowered.server.game.version.MinecraftVersionCache
import org.lanternpowered.server.network.protocol.Protocol
import org.lanternpowered.server.plugin.LanternPluginManager
import org.lanternpowered.server.registry.LanternGameRegistry
import org.lanternpowered.server.scheduler.LanternScheduler
import org.lanternpowered.server.service.LanternServiceProvider
import org.lanternpowered.server.service.permission.LanternContextCalculator
import org.lanternpowered.server.service.permission.LanternPermissionService
import org.lanternpowered.server.sql.LanternSqlManager
import org.lanternpowered.server.util.LocaleCache
import org.lanternpowered.server.util.palette.LanternPaletteBasedArrayFactory
import org.spongepowered.api.SystemSubject
import org.spongepowered.api.asset.AssetManager
import org.spongepowered.api.command.manager.CommandManager
import org.spongepowered.api.config.ConfigManager
import org.spongepowered.api.data.DataManager
import org.spongepowered.api.network.channel.ChannelRegistry
import org.spongepowered.api.scheduler.Scheduler
import org.spongepowered.api.service.ban.BanService
import org.spongepowered.api.service.economy.EconomyService
import org.spongepowered.api.service.pagination.PaginationService
import org.spongepowered.api.service.permission.PermissionService
import org.spongepowered.api.service.whitelist.WhitelistService
import org.spongepowered.api.sql.SqlManager
import org.spongepowered.api.util.metric.MetricsConfigManager
import java.nio.file.Path
import java.nio.file.Paths
import java.util.Locale
import java.util.concurrent.ScheduledExecutorService

object LanternGame : Game {

    /**
     * The current protocol version number that's supported.
     */
    const val PROTOCOL_VERSION = 714

    /**
     * The logger.
     */
    val logger: Logger = LogManager.getLogger("lantern")

    private lateinit var platform: Platform
    private lateinit var pluginManager: LanternPluginManager
    private lateinit var registry: LanternGameRegistry
    private lateinit var eventManager: LanternEventManager
    private lateinit var sqlManager: LanternSqlManager
    private lateinit var serviceProvider: LanternServiceProvider
    private lateinit var configManager: LanternConfigManager
    private lateinit var server: LanternServerNew
    private lateinit var gameDirectory: Path

    /**
     * The current minecraft version.
     */
    lateinit var minecraftVersion: MinecraftVersion
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
    val syncExecutor: ScheduledExecutorService = TODO()
    val syncScheduler: LanternScheduler = TODO()

    override val paletteBasedArrayFactory: PaletteBasedArrayFactory
        get() = LanternPaletteBasedArrayFactory

    override val injector: Injector
        get() = TODO("Not yet implemented")

    fun init(options: OptionSet) {
        this.gameDirectory = Paths.get("")
        this.configDirectory = this.gameDirectory.resolve(options.valueOf(LaunchOptions.CONFIG_DIRECTORY) ?: "config")

        this.eventManager = LanternEventManager
        this.configManager = LanternConfigManager(this.logger, this.configDirectory)
        this.sqlManager = LanternSqlManager(this.configManager)
        this.serviceProvider = LanternServiceProvider(this.eventManager)

        val pluginsDirectory = this.gameDirectory.resolve(options.valueOf(LaunchOptions.PLUGINS_DIRECTORY) ?: "plugins")

        // Construct all the plugins
        this.pluginManager = LanternPluginManager(this, this.logger, this.eventManager, this.gameDirectory, pluginsDirectory)
        this.pluginManager.instantiate()

        this.platform = LanternPlatform(mapOf(
                PlatformComponent.API to this.spongeApiPlugin,
                PlatformComponent.GAME to this.minecraftPlugin,
                PlatformComponent.IMPLEMENTATION to this.lanternPlugin
        ))

        this.minecraftVersionCache = MinecraftVersionCache()
        this.minecraftVersionCache.init()

        this.minecraftVersion = LanternMinecraftVersion(
                this.minecraftPlugin.metadata.version, Protocol.CURRENT_VERSION, false)

        val versionCacheEntry = this.minecraftVersionCache.getVersionOrUnknown(Protocol.CURRENT_VERSION, false)
        check(versionCacheEntry == this.minecraftVersion) {
            "The current version and version in the cache don't match: $minecraftVersion != $versionCacheEntry" }

        // Call the plugin construct lifecycle events
        this.pluginManager.construct()

        // Register all factories, builders and catalog registries
        this.registry = LanternGameRegistry(this)
        this.registry.init()

        initPermissionService()

        this.serviceProvider.register<EconomyService>()
        this.serviceProvider.register<WhitelistService>() // TODO
        this.serviceProvider.register<BanService>() // TODO
        this.serviceProvider.register<PaginationService>() // TODO

        // And... done
        this.eventManager.post(LanternLoadedGameEvent(this))
    }

    /**
     * Sets the server instance.
     */
    fun setServer(server: LanternServerNew) { this.server = server }

    /**
     * Initializes the permission service.
     */
    private fun initPermissionService() {
        val permissionService = this.serviceProvider.register<PermissionService> {
            this.lanternPlugin to LanternPermissionService()
        }
        permissionService.registerContextCalculator(LanternContextCalculator())
    }

    override fun getMetricsConfigManager(): MetricsConfigManager {
        TODO("Not yet implemented")
    }

    override fun getServer(): Server = this.server
    override fun getRegistry(): GameRegistry = this.registry
    override fun getPluginManager(): PluginManager = this.pluginManager
    override fun getPlatform(): Platform = this.platform
    override fun getSqlManager(): SqlManager = this.sqlManager
    override fun getEventManager(): EventManager = this.eventManager
    override fun getConfigManager(): ConfigManager = this.configManager
    override fun getServiceProvider(): LanternServiceProvider = this.serviceProvider
    override fun getGameDirectory(): Path = this.gameDirectory

    override fun getLocale(locale: String): Locale = LocaleCache[locale]

    override fun getCommandManager(): CommandManager {
        TODO("Not yet implemented")
    }

    override fun getAsyncScheduler(): Scheduler {
        TODO("Not yet implemented")
    }

    override fun getAssetManager(): AssetManager {
        TODO("Not yet implemented")
    }

    override fun getSystemSubject(): SystemSubject {
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
