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
import org.lanternpowered.api.cause.causeOf
import org.lanternpowered.server.registry.LanternGameRegistry
import org.lanternpowered.server.util.LocaleCache
import org.spongepowered.api.SystemSubject
import org.spongepowered.api.event.EventManager
import org.spongepowered.api.scheduler.Scheduler
import java.nio.file.Path
import java.util.Locale

class LanternGame : Game {

    companion object {

        /**
         * The current protocol version number that's supported.
         */
        const val PROTOCOL_VERSION = 713
    }

    private var state: GameState? = null

    /**
     * Switches to the next [state].
     *
     * Passing the [state] makes it clear when a state
     * should happen, and validates against wrong switches.
     */
    fun switchState(state: GameState) {
        val currentState = this.state
        check(currentState == null || state.ordinal == currentState.ordinal + 1) {
            "It's only possible to the next state. Attempted to switch from $currentState to $state."
        }

        this.state = state

        val cause = causeOf(this)
        val event = state.createEvent(cause)
        this.eventManager.post(event)
    }

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
