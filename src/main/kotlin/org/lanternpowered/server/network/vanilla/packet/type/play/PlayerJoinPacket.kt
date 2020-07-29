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

import org.lanternpowered.api.data.persistence.DataView
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.server.network.packet.Packet
import org.spongepowered.api.entity.living.player.gamemode.GameMode

/**
 * @property gameMode The game mode of the player
 * @property worldName The world this player is currently in
 * @property entityId The entity id of the player
 * @property playerListSize The size of the player list
 * @property hasReducedDebug Whether less debug info should be displayed in the debug screen
 * @property isHardcore Whether the hardcore mode is enabled
 * @property isFlat Whether the world has a lower horizon, e.g. in a flat world
 * @property viewDistance The view distance, in chunks
 * @property enableRespawnScreen Whether the respawn screen is shown when the player dies
 * @property seed The seed of the world
 */
data class PlayerJoinPacket(
        val dimension: NamespacedKey,
        val worldName: NamespacedKey,
        val gameMode: GameMode,
        val previousGameMode: GameMode,
        val dimensionRegistry: List<DimensionRegistryEntry>,
        val biomeRegistry: List<BiomeRegistryEntry>,
        val entityId: Int,
        val playerListSize: Int,
        val hasReducedDebug: Boolean,
        val isHardcore: Boolean,
        val isFlat: Boolean,
        val isDebug: Boolean,
        val viewDistance: Int,
        val enableRespawnScreen: Boolean,
        val seed: Long
) : Packet

data class DimensionRegistryEntry(
        val key: NamespacedKey,
        val data: DataView
)

data class BiomeRegistryEntry(
        val key: NamespacedKey,
        val data: DataView
)
