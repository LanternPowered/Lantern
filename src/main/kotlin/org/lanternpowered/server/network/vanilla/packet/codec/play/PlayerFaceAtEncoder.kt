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
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerFaceAtPacket

object PlayerFaceAtEncoder : PacketEncoder<PlayerFaceAtPacket> {

    override fun encode(ctx: CodecContext, packet: PlayerFaceAtPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeVarInt(packet.sourceBodyPosition.ordinal)
        val pos = packet.position
        buf.writeDouble(pos.x)
        buf.writeDouble(pos.y)
        buf.writeDouble(pos.z)
        val flag = packet is PlayerFaceAtPacket.Entity
        buf.writeBoolean(flag)
        if (flag) {
            val (_, _, entityId, entityBodyPosition) = packet as PlayerFaceAtPacket.Entity
            buf.writeVarInt(entityId)
            buf.writeVarInt(entityBodyPosition.ordinal)
        }
        return buf
    }
}
