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
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginEncryptionResponsePacket

class LoginEncryptionResponseCodec : Codec<LoginEncryptionResponsePacket> {

    override fun decode(context: CodecContext, buf: ByteBuffer): LoginEncryptionResponsePacket {
        val sharedSecret = buf.readByteArray()
        val verifyToken = buf.readByteArray()
        return LoginEncryptionResponsePacket(sharedSecret, verifyToken)
    }
}
