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

data class PlayerRespawnPacket(
        val gameMode: GameMode,
        val dimensionType: DimensionType,
        val lowHorizon: Boolean,
        val seed: Long,
        val copyMetadata: Boolean = false
) : Packet
