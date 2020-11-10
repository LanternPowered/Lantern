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
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginEncryptionRequestPacket

object LoginEncryptionRequestEncoder : PacketEncoder<LoginEncryptionRequestPacket> {

    override fun encode(ctx: CodecContext, packet: LoginEncryptionRequestPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeString("")
        buf.writeByteArray(packet.publicKey)
        buf.writeByteArray(packet.verifyToken)
        return buf
    }
}
