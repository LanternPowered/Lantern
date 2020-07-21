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

import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.network.ProxyType
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.codec.Codec
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.handshake.HandshakePacket

class HandshakeCodec : Codec<HandshakePacket> {

    override fun decode(context: CodecContext, buf: ByteBuffer): HandshakePacket {
        val protocol = buf.readVarInt()
        val hostname = if (Lantern.getGame().globalConfig.proxyType !== ProxyType.NONE) {
            buf.readString()
        } else {
            buf.readLimitedString(255)
        }
        val port = buf.readShort()
        val state = buf.readVarInt()
        return HandshakePacket(state, hostname, port.toInt(), protocol)
    }
}
