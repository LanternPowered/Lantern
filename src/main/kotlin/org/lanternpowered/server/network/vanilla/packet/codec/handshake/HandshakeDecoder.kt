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
package org.lanternpowered.server.network.vanilla.packet.codec.handshake

import org.lanternpowered.server.network.ProxyType
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.PacketDecoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.handshake.HandshakePacket

object HandshakeDecoder : PacketDecoder<HandshakePacket> {

    override fun decode(ctx: CodecContext, buf: ByteBuffer): HandshakePacket {
        val protocol = buf.readVarInt()
        val hostname = if (ctx.game.config.server.proxy.type != ProxyType.NONE) {
            buf.readString()
        } else {
            buf.readLimitedString(255)
        }
        val port = buf.readShort()
        val state = buf.readVarInt()
        return HandshakePacket(state, hostname, port.toInt(), protocol)
    }
}
