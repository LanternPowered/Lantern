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
package org.lanternpowered.server.network.vanilla.message.type.play

import org.lanternpowered.server.network.message.Message
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.world.dimension.DimensionType

data class MessagePlayOutPlayerJoinGame(
        /**
         * The game mode of the player.
         */
        val gameMode: GameMode,
        /**
         * The dimension type of the world this player is currently in.
         */
        val dimensionType: DimensionType,
        /**
         * The entity id of the player.
         */
        val entityId: Int,
        /**
         * The size of the player list.
         */
        val playerListSize: Int,
        /**
         * Whether reduced debug should be used, no idea what this will do,
         * maybe less information in the f3 screen?
         */
        val reducedDebug: Boolean,
        /**
         * Whether the hardcore mode is enabled.
         */
        val isHardcore: Boolean,
        val lowHorizon: Boolean,
        val viewDistance: Int,
        /**
         * Whether the respawn screen on death is shown.
         */
        val enableRespawnScreen: Boolean,
        /**
         * The seed of the world.
         */
        val seed: Long
) : Message
