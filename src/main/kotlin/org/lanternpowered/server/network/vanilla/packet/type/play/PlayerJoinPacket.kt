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
package org.lanternpowered.server.network.vanilla.packet.type.play

import org.lanternpowered.server.network.message.Packet
import org.spongepowered.api.entity.living.player.gamemode.GameMode
import org.spongepowered.api.world.dimension.DimensionType

/**
 * @property gameMode The game mode of the player
 * @property dimensionType The dimension type of the world this player is currently in
 * @property entityId The entity id of the player
 * @property playerListSize The size of the player list
 * @property reducedDebug Whether less debug info should be displayed in the debug screen
 * @property isHardcore Whether the hardcore mode is enabled
 * @property lowHorizon Whether the world has a lower horizon, e.g. in a flat world
 * @property viewDistance The view distance, in chunks
 * @property enableRespawnScreen Whether the respawn screen is shown when the player dies
 * @property seed The seed of the world
 */
data class PlayerJoinPacket(
        val gameMode: GameMode,
        val dimensionType: DimensionType,
        val entityId: Int,
        val playerListSize: Int,
        val reducedDebug: Boolean,
        val isHardcore: Boolean,
        val lowHorizon: Boolean,
        val viewDistance: Int,
        val enableRespawnScreen: Boolean,
        val seed: Long
) : Packet
