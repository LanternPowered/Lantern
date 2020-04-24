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

import io.netty.handler.codec.EncoderException
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.message.Message
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEffect
import org.lanternpowered.server.network.vanilla.message.type.play.SetMusicDiscMessage
import org.lanternpowered.server.registry.type.data.MusicDiscRegistry

class CodecPlayOutEffect : Codec<Message> {

    override fun encode(context: CodecContext, message: Message): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            when (message) {
                is MessagePlayOutEffect -> {
                    writeInt(message.type)
                    writePosition(message.position)
                    writeInt(message.data)
                    writeBoolean(message.isBroadcast)
                }
                is SetMusicDiscMessage -> {
                    writeInt(1010)
                    writePosition(message.position)
                    val id = if (message.musicDisc == null) 0 else 2256 + MusicDiscRegistry.getId(message.musicDisc)
                    writeInt(id)
                    writeBoolean(false)
                }
                else -> throw EncoderException("Unsupported message type: " + message.javaClass.name)
            }
        }
    }
}
