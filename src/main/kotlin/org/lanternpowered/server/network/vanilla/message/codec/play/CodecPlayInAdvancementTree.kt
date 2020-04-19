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
package org.lanternpowered.server.network.vanilla.message.codec.play

import io.netty.handler.codec.DecoderException
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInAdvancementTree

class CodecPlayInAdvancementTree : Codec<MessagePlayInAdvancementTree> {

    override fun decode(context: CodecContext, buf: ByteBuffer): MessagePlayInAdvancementTree {
        val type = buf.readVarInt()
        if (type == 0) {
            val id = buf.readString()
            return MessagePlayInAdvancementTree.Open(id)
        } else if (type == 1) {
            return MessagePlayInAdvancementTree.Close
        }
        throw DecoderException("Unknown type: $type")
    }
}
