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
package org.lanternpowered.server.event.lifecycle

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.Game
import org.lanternpowered.api.Server
import org.lanternpowered.api.cause.Cause
import org.lanternpowered.api.cause.emptyCause
import org.lanternpowered.api.event.lifecycle.ConstructPluginEvent
import org.lanternpowered.api.event.lifecycle.LifecycleEvent
import org.lanternpowered.api.event.lifecycle.ServerLifecycleEvent
import org.lanternpowered.api.event.lifecycle.StartedServerEvent
import org.lanternpowered.api.event.lifecycle.StartingServerEvent
import org.lanternpowered.api.event.lifecycle.StoppingServerEvent
import org.lanternpowered.api.plugin.PluginContainer
import org.lanternpowered.api.util.type.typeTokenOf

abstract class LanternLifecycleEvent(
        private val cause: Cause,
        private val game: Game
) : LifecycleEvent {
    override fun getCause(): Cause = this.cause
    override fun getGame(): Game = this.game
}

abstract class LanternServerLifecycleEvent(
        cause: Cause, game: Game, private val server: Server
) : LanternLifecycleEvent(cause, game), ServerLifecycleEvent {
    override fun getGenericType(): TypeToken<Server> = typeTokenOf()
    override fun getEngine(): Server = this.server
}

class LanternConstructPluginEvent(
        game: Game, private val plugin: PluginContainer, cause: Cause = emptyCause()
) : LanternLifecycleEvent(cause, game), ConstructPluginEvent {
    override fun getPlugin(): PluginContainer = this.plugin
}

class LanternStartingServerEvent(game: Game, server: Server, cause: Cause = emptyCause()) :
        LanternServerLifecycleEvent(cause, game, server), StartingServerEvent

class LanternStartedServerEvent(game: Game, server: Server, cause: Cause = emptyCause()) :
        LanternServerLifecycleEvent(cause, game, server), StartedServerEvent

class LanternStoppingServerEvent(game: Game, server: Server, cause: Cause = emptyCause()) :
        LanternServerLifecycleEvent(cause, game, server), StoppingServerEvent
