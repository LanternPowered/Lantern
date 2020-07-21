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

import io.netty.handler.codec.DecoderException
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.codec.Codec
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.ChangeAdvancementTreePacket

class ChangeAdvancementTreeCodec : Codec<ChangeAdvancementTreePacket> {

    override fun decode(context: CodecContext, buf: ByteBuffer): ChangeAdvancementTreePacket {
        val type = buf.readVarInt()
        if (type == 0) {
            val id = buf.readString()
            return ChangeAdvancementTreePacket.Open(id)
        } else if (type == 1) {
            return ChangeAdvancementTreePacket.Close
        }
        throw DecoderException("Unknown type: $type")
    }
}
