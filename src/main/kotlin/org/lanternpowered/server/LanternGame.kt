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

import org.lanternpowered.api.Game
import org.lanternpowered.api.GameState
import org.lanternpowered.api.Server
import org.lanternpowered.api.injector.Injector
import org.lanternpowered.api.util.palette.PaletteBasedArrayFactory
import org.lanternpowered.server.event.LanternEventManager
import org.lanternpowered.server.registry.LanternGameRegistry
import org.lanternpowered.server.scheduler.LanternScheduler
import org.lanternpowered.server.util.LocaleCache
import org.lanternpowered.server.util.palette.LanternPaletteBasedArrayFactory
import org.spongepowered.api.Platform
import org.spongepowered.api.SystemSubject
import org.spongepowered.api.asset.AssetManager
import org.spongepowered.api.command.manager.CommandManager
import org.spongepowered.api.config.ConfigManager
import org.spongepowered.api.data.DataManager
import org.spongepowered.api.event.EventManager
import org.spongepowered.api.network.channel.ChannelRegistry
import org.spongepowered.api.plugin.PluginManager
import org.spongepowered.api.scheduler.Scheduler
import org.spongepowered.api.service.ServiceProvider
import org.spongepowered.api.sql.SqlManager
import org.spongepowered.api.util.metric.MetricsConfigManager
import java.nio.file.Path
import java.util.Locale
import java.util.concurrent.ScheduledExecutorService

object LanternGame : Game {

    /**
     * The current protocol version number that's supported.
     */
    const val PROTOCOL_VERSION = 714

    private var state: GameState? = null

    val syncExecutor: ScheduledExecutorService = TODO()
    val syncScheduler: LanternScheduler = TODO()

    override val paletteBasedArrayFactory: PaletteBasedArrayFactory
        get() = LanternPaletteBasedArrayFactory

    override val injector: Injector
        get() = TODO("Not yet implemented")

    override fun getMetricsConfigManager(): MetricsConfigManager {
        TODO("Not yet implemented")
    }

    override fun getSqlManager(): SqlManager {
        TODO("Not yet implemented")
    }

    override fun getEventManager(): EventManager = LanternEventManager

    override fun getLocale(locale: String): Locale = LocaleCache[locale]

    override fun getPluginManager(): PluginManager {
        TODO("Not yet implemented")
    }

    override fun getPlatform(): Platform {
        TODO("Not yet implemented")
    }

    override fun getCommandManager(): CommandManager {
        TODO("Not yet implemented")
    }

    override fun getRegistry() = LanternGameRegistry

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

    override fun getGameDirectory(): Path {
        TODO("Not yet implemented")
    }

    override fun getServer(): Server {
        TODO("Not yet implemented")
    }

    override fun getConfigManager(): ConfigManager {
        TODO("Not yet implemented")
    }

    override fun getChannelRegistry(): ChannelRegistry {
        TODO("Not yet implemented")
    }

    override fun getServiceProvider(): ServiceProvider {
        TODO("Not yet implemented")
    }

    override fun isServerAvailable(): Boolean = true
}
