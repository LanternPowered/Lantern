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
package org.lanternpowered.server.network.vanilla.message.codec.handshake

import org.lanternpowered.server.game.Lantern
import org.lanternpowered.server.network.ProxyType
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.handshake.HandshakeMessage

class HandshakeCodec : Codec<HandshakeMessage> {

    override fun decode(context: CodecContext, buf: ByteBuffer): HandshakeMessage {
        val protocol = buf.readVarInt()
        val hostname = if (Lantern.getGame().globalConfig.proxyType !== ProxyType.NONE) {
            buf.readString()
        } else {
            buf.readLimitedString(255)
        }
        val port = buf.readShort()
        val state = buf.readVarInt()
        return HandshakeMessage(state, hostname, port.toInt(), protocol)
    }
}
