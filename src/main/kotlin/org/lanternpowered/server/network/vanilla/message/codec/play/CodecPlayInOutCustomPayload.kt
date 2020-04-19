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
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutBrand
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload

class CodecPlayInOutCustomPayload : AbstractCodecPlayInOutCustomPayload() {

    override fun encode0(context: CodecContext, message: Message): AbstractCodecPlayInOutCustomPayload.MessageResult {
        if (message is MessagePlayInOutBrand) {
            val content = context.byteBufAlloc().buffer().writeString(message.brand)
            return AbstractCodecPlayInOutCustomPayload.MessageResult("minecraft:brand", content)
        }
        throw EncoderException("Unsupported message type: $message")
    }

    override fun decode0(context: CodecContext, channel: String, content: ByteBuffer): Message {
        if (channel == "minecraft:brand") {
            return MessagePlayInOutBrand(content.readString())
        }
        content.retain() // Retain the content until we can process it
        return MessagePlayInOutChannelPayload(channel, content)
    }
}
