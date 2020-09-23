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
import org.lanternpowered.server.network.text.NetworkText
import org.lanternpowered.server.network.vanilla.packet.type.play.ChatMessagePacket

object ChatMessageEncoder : PacketEncoder<ChatMessagePacket> {

    override fun encode(ctx: CodecContext, packet: ChatMessagePacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        NetworkText.write(ctx, buf, packet.message)
        buf.writeByte(packet.type.ordinal.toByte())
        if (packet.sender != null) {
            buf.writeUniqueId(packet.sender)
        } else {
            buf.writeLong(0L)
            buf.writeLong(0L)
        }
        return buf
    }
}
