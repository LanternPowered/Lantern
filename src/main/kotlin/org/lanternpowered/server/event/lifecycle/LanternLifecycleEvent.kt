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
import org.spongepowered.api.event.lifecycle.LoadedGameEvent

abstract class LanternLifecycleEvent(
        private val game: Game,
        private val cause: Cause
) : LifecycleEvent {
    override fun getCause(): Cause = this.cause
    override fun getGame(): Game = this.game
}

abstract class LanternServerLifecycleEvent(
        game: Game, private val server: Server, cause: Cause
) : LanternLifecycleEvent(game, cause), ServerLifecycleEvent {
    override fun getGenericType(): TypeToken<Server> = typeTokenOf()
    override fun getEngine(): Server = this.server
}

class LanternConstructPluginEvent(
        game: Game, private val plugin: PluginContainer, cause: Cause = emptyCause()
) : LanternLifecycleEvent(game, cause), ConstructPluginEvent {
    override fun getPlugin(): PluginContainer = this.plugin
}

class LanternStartingServerEvent(game: Game, server: Server, cause: Cause = emptyCause()) :
        LanternServerLifecycleEvent(game, server, cause), StartingServerEvent

class LanternStartedServerEvent(game: Game, server: Server, cause: Cause = emptyCause()) :
        LanternServerLifecycleEvent(game, server, cause), StartedServerEvent

class LanternStoppingServerEvent(game: Game, server: Server, cause: Cause = emptyCause()) :
        LanternServerLifecycleEvent(game, server, cause), StoppingServerEvent

class LanternLoadedGameEvent(game: Game, cause: Cause = emptyCause()) : LanternLifecycleEvent(game, cause), LoadedGameEvent
