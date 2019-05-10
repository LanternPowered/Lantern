/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
