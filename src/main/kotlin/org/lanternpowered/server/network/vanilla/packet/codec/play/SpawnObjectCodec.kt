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
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnObjectPacket

class SpawnObjectCodec : Codec<SpawnObjectPacket> {

    override fun encode(context: CodecContext, packet: SpawnObjectPacket): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            writeVarInt(packet.entityId)
            writeUniqueId(packet.uniqueId)
            writeVarInt(packet.objectType)
            writeVector3d(packet.position)
            writeByte(packet.pitch.toByte())
            writeByte(packet.yaw.toByte())
            writeInt(packet.objectData)
            val velocity = packet.velocity
            writeShort((velocity.x * 8000.0).coerceAtMost(Short.MAX_VALUE.toDouble()).toShort())
            writeShort((velocity.y * 8000.0).coerceAtMost(Short.MAX_VALUE.toDouble()).toShort())
            writeShort((velocity.z * 8000.0).coerceAtMost(Short.MAX_VALUE.toDouble()).toShort())
        }
    }
}
