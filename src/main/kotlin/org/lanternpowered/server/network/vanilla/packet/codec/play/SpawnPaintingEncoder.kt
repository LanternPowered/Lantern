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
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnPaintingPacket
import org.lanternpowered.server.registry.type.data.ArtTypeRegistry
import org.spongepowered.api.util.Direction

object SpawnPaintingEncoder : PacketEncoder<SpawnPaintingPacket> {

    override fun encode(ctx: CodecContext, packet: SpawnPaintingPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeVarInt(packet.entityId)
        buf.writeUniqueId(packet.uniqueId)
        buf.writeVarInt(ArtTypeRegistry.getId(packet.artType))
        buf.writeBlockPosition(packet.position)
        buf.writeByte(packet.direction.getId())
        return buf
    }

    private fun Direction.getId(): Byte = when (this) {
        Direction.EAST -> 3
        Direction.NORTH -> 2
        Direction.WEST -> 1
        else -> 0
    }
}
