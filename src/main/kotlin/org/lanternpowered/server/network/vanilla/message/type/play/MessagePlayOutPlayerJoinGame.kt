/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.vanilla.message.type.play

import org.lanternpowered.server.network.message.Message
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.world.DimensionType

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
        val viewDistance: Int
) : Message
