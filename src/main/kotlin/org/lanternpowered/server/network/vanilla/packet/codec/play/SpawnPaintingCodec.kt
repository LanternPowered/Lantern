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
package org.lanternpowered.server.network.vanilla.packet.codec.play

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.codec.Codec
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnPaintingPacket
import org.lanternpowered.server.registry.type.data.ArtTypeRegistry
import org.spongepowered.api.util.Direction

class SpawnPaintingCodec : Codec<SpawnPaintingPacket> {

    override fun encode(context: CodecContext, packet: SpawnPaintingPacket): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            writeVarInt(packet.entityId)
            writeUniqueId(packet.uniqueId)
            writeVarInt(ArtTypeRegistry.getId(packet.artType))
            writePosition(packet.position)
            writeByte(packet.direction.getId())
        }
    }

    private fun Direction.getId(): Byte = when (this) {
        Direction.EAST -> 3
        Direction.NORTH -> 2
        Direction.WEST -> 1
        else -> 0
    }
}
