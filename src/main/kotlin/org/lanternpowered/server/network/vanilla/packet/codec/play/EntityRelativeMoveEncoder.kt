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
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityRelativeMovePacket

object EntityRelativeMoveEncoder : PacketEncoder<EntityRelativeMovePacket> {

    override fun encode(ctx: CodecContext, packet: EntityRelativeMovePacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeVarInt(packet.entityId)
        buf.writeShort(packet.deltaX.toShort())
        buf.writeShort(packet.deltaY.toShort())
        buf.writeShort(packet.deltaZ.toShort())
        buf.writeBoolean(packet.isOnGround)
        return buf
    }
}
