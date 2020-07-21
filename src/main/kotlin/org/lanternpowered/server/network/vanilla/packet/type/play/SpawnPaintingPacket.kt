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

import org.lanternpowered.server.network.packet.Packet
import org.spongepowered.api.data.type.ArtType
import org.spongepowered.api.util.Direction
import org.spongepowered.math.vector.Vector3i
import java.util.UUID

data class SpawnPaintingPacket(
        val entityId: Int,
        val uniqueId: UUID,
        val artType: ArtType,
        val position: Vector3i,
        val direction: Direction
) : Packet
