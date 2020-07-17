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

import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import org.lanternpowered.api.Game
import org.lanternpowered.api.Platform
import org.lanternpowered.api.PlatformComponent
import org.lanternpowered.api.Server
import org.lanternpowered.api.cause.emptyCause
import org.lanternpowered.api.event.EventManager
import org.lanternpowered.api.injector.Injector
import org.lanternpowered.api.plugin.PluginContainer
import org.lanternpowered.api.plugin.PluginManager
import org.lanternpowered.api.registry.GameRegistry
import org.lanternpowered.api.util.palette.PaletteBasedArrayFactory
import org.lanternpowered.server.console.LanternConsole
import org.lanternpowered.server.event.LanternEventManager
import org.lanternpowered.server.game.LanternPlatform
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
import org.spongepowered.api.event.cause.Cause
import org.spongepowered.api.event.lifecycle.LoadedGameEvent
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
    private lateinit var server: LanternServerNew
    private lateinit var gameDirectory: Path

    val minecraftPlugin: PluginContainer
        get() = this.pluginManager.minecraftPlugin

    val lanternPlugin: PluginContainer
        get() = this.pluginManager.lanternPlugin

    val spongePlugin: PluginContainer
        get() = this.pluginManager.spongePlugin

    val syncExecutor: ScheduledExecutorService = TODO()
    val syncScheduler: LanternScheduler = TODO()

    override val paletteBasedArrayFactory: PaletteBasedArrayFactory
        get() = LanternPaletteBasedArrayFactory

    override val injector: Injector
        get() = TODO("Not yet implemented")

    fun init() {
        this.gameDirectory = Paths.get("")

        // Construct the event manager
        this.eventManager = LanternEventManager
        this.sqlManager = LanternSqlManager()
        this.serviceProvider = LanternServiceProvider()

        // Construct all the plugins
        this.pluginManager = LanternPluginManager(this, this.logger, this.eventManager, this.gameDirectory)
        this.pluginManager.instantiate()

        this.platform = LanternPlatform(mapOf(
                PlatformComponent.API to this.pluginManager.spongeApiPlugin,
                PlatformComponent.GAME to this.pluginManager.minecraftPlugin,
                PlatformComponent.IMPLEMENTATION to this.pluginManager.lanternPlugin
        ))

        // Call the plugin construct lifecycle events
        this.pluginManager.construct()

        // Register all factories, builders and catalog registries
        this.registry = LanternGameRegistry(this)
        this.registry.init()

        // Register all the services
        initServices()

        // And... done
        val cause = emptyCause()
        val event = object : LoadedGameEvent {
            override fun getCause(): Cause = cause
            override fun getGame(): Game = this@LanternGame
        }
        this.eventManager.post(event)
    }

    /**
     * Sets the server instance.
     */
    fun setServer(server: LanternServerNew) { this.server = server }

    /**
     * Initializes the services. This calls the provide service events.
     */
    private fun initServices() {
        initPermissionService()

        this.serviceProvider.register<EconomyService>()
        this.serviceProvider.register<WhitelistService>() // TODO
        this.serviceProvider.register<BanService>() // TODO
        this.serviceProvider.register<PaginationService>() // TODO
    }

    private fun initPermissionService() {
        val permissionService = this.serviceProvider.register<PermissionService> {
            this.lanternPlugin to LanternPermissionService()
        }
        permissionService.registerContextCalculator(LanternContextCalculator())

        // Initialize console permissions
        LanternConsole.containingCollection
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

    override fun getConfigManager(): ConfigManager {
        TODO("Not yet implemented")
    }

    override fun getChannelRegistry(): ChannelRegistry {
        TODO("Not yet implemented")
    }

    override fun isServerAvailable(): Boolean = this::server.isInitialized
}
