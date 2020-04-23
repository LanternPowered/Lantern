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

import org.lanternpowered.api.GameState
import org.lanternpowered.api.cause.Cause
import org.lanternpowered.api.event.LanternEventFactory
import org.spongepowered.api.event.game.state.GameStateEvent

fun GameState.createEvent(cause: Cause): GameStateEvent = when (this) {
    GameState.CONSTRUCTION -> LanternEventFactory.createGameConstructionEvent(cause)
    GameState.PRE_INITIALIZATION -> LanternEventFactory.createGamePreInitializationEvent(cause)
    GameState.INITIALIZATION -> LanternEventFactory.createGameInitializationEvent(cause)
    GameState.POST_INITIALIZATION -> LanternEventFactory.createGamePostInitializationEvent(cause)
    GameState.LOAD_COMPLETE -> LanternEventFactory.createGameLoadCompleteEvent(cause)
    GameState.SERVER_ABOUT_TO_START -> LanternEventFactory.createGameAboutToStartServerEvent(cause)
    GameState.SERVER_STARTING -> LanternEventFactory.createGameStartingServerEvent(cause)
    GameState.SERVER_STARTED -> LanternEventFactory.createGameStartedServerEvent(cause)
    GameState.SERVER_STOPPING -> LanternEventFactory.createGameStoppingServerEvent(cause)
    GameState.SERVER_STOPPED -> LanternEventFactory.createGameStoppedServerEvent(cause)
    GameState.GAME_STOPPING -> LanternEventFactory.createGameStoppingEvent(cause)
    GameState.GAME_STOPPED -> LanternEventFactory.createGameStoppingEvent(cause)
}
