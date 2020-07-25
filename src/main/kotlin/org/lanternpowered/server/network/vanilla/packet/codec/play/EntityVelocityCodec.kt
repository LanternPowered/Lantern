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
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityVelocityPacket

object EntityVelocityCodec : PacketEncoder<EntityVelocityPacket> {

    override fun encode(context: CodecContext, packet: EntityVelocityPacket): ByteBuffer {
        val buf = context.byteBufAlloc().buffer()
        buf.writeVarInt(packet.entityId)
        buf.writeShort((packet.x * 8000.0).coerceAtMost(Short.MAX_VALUE.toDouble()).toShort())
        buf.writeShort((packet.y * 8000.0).coerceAtMost(Short.MAX_VALUE.toDouble()).toShort())
        buf.writeShort((packet.z * 8000.0).coerceAtMost(Short.MAX_VALUE.toDouble()).toShort())
        return buf
    }
}
