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

import org.lanternpowered.api.world.chunk.ChunkPosition
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.world.chunk.LocalPosition

/**
 * @property chunk The chunk position to which blocks will be updated relatively
 * @property changes The block changes relative to the [chunk] position (0..15 offset)
 * @property inverseTrustEdges Always inverse of the preceding update light packet (TODO: Figure out what this does)
 */
class MultiBlockChangePacket(
        val chunk: ChunkPosition,
        val inverseTrustEdges: Boolean,
        val changes: Collection<Entry>
) : Packet {

    data class Entry(val position: LocalPosition, val blockState: Int)
}
