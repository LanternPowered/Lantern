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
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnObjectPacket

object SpawnObjectEncoder : PacketEncoder<SpawnObjectPacket> {

    override fun encode(ctx: CodecContext, packet: SpawnObjectPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeVarInt(packet.entityId)
        buf.writeUniqueId(packet.uniqueId)
        buf.writeVarInt(packet.objectType)
        buf.writeVector3d(packet.position)
        buf.writeByte(packet.pitch.packed)
        buf.writeByte(packet.yaw.packed)
        buf.writeInt(packet.objectData)
        val velocity = packet.velocity
        buf.writeShort((velocity.x * 8000.0).coerceAtMost(Short.MAX_VALUE.toDouble()).toShort())
        buf.writeShort((velocity.y * 8000.0).coerceAtMost(Short.MAX_VALUE.toDouble()).toShort())
        buf.writeShort((velocity.z * 8000.0).coerceAtMost(Short.MAX_VALUE.toDouble()).toShort())
        return buf
    }
}
