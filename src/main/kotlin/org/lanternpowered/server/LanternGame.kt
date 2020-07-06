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
import org.lanternpowered.server.registry.LanternGameRegistry
import org.lanternpowered.server.scheduler.LanternScheduler
import org.lanternpowered.server.util.LocaleCache
import org.lanternpowered.server.util.palette.LanternPaletteBasedArrayFactory
import org.spongepowered.api.SystemSubject
import org.spongepowered.api.event.EventManager
import org.spongepowered.api.scheduler.Scheduler
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

    override fun getState(): GameState = checkNotNull(this.state) { "The initial state isn't set yet." }
    override fun getLocale(locale: String): Locale = LocaleCache[locale]
    override fun getRegistry() = LanternGameRegistry

    override fun getEventManager(): EventManager {
        return super.getEventManager()
    }

    override fun getAsyncScheduler(): Scheduler {
        TODO("Not yet implemented")
    }

    override fun getSystemSubject(): SystemSubject {
        TODO("Not yet implemented")
    }

    override fun getGameDirectory(): Path {
        TODO("Not yet implemented")
    }

    override fun getServer(): Server {
        TODO("Not yet implemented")
    }

    override fun isServerAvailable(): Boolean = true
}
