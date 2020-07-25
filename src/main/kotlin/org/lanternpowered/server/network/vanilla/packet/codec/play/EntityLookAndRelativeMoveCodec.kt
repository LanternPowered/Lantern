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
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityLookAndRelativeMovePacket

object EntityLookAndRelativeMoveCodec : PacketEncoder<EntityLookAndRelativeMovePacket> {

    override fun encode(context: CodecContext, packet: EntityLookAndRelativeMovePacket): ByteBuffer {
        val buf = context.byteBufAlloc().buffer()
        buf.writeVarInt(packet.entityId)
        buf.writeShort(packet.deltaX.toShort())
        buf.writeShort(packet.deltaY.toShort())
        buf.writeShort(packet.deltaZ.toShort())
        buf.writeByte(packet.yaw)
        buf.writeByte(packet.pitch)
        buf.writeBoolean(packet.isOnGround)
        return buf
    }
}
