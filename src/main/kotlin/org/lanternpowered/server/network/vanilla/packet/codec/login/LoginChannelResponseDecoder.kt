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
package org.lanternpowered.server.network.vanilla.packet.codec.login

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.PacketDecoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginChannelResponsePacket

object LoginChannelResponseDecoder : PacketDecoder<LoginChannelResponsePacket> {

    override fun decode(ctx: CodecContext, buf: ByteBuffer): LoginChannelResponsePacket {
        val transactionId = buf.readVarInt()
        val content: ByteBuffer
        if (buf.readBoolean()) { // Whether content is following
            content = buf.slice() // Slice content for performance over copying
            content.retain() // Retain the buffer until released
        } else {
            // Just a empty buffer
            content = ctx.byteBufAlloc().buffer(0)
        }
        return LoginChannelResponsePacket(transactionId, content)
    }
}
