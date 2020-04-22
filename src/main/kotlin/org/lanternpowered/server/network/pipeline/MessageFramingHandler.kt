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
/*
 * Copyright (c) 2011-2014 Glowstone - Tad Hardesty
 * Copyright (c) 2010-2011 Lightstone - Graham Edgecombe
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.pipeline

import io.netty.buffer.ByteBuf
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.ByteToMessageCodec
import io.netty.handler.codec.DecoderException
import org.lanternpowered.server.network.buffer.LanternByteBuffer

class MessageFramingHandler : ByteToMessageCodec<ByteBuf>() {

    override fun encode(ctx: ChannelHandlerContext, buf: ByteBuf, output: ByteBuf) {
        LanternByteBuffer.writeVarInt(output, buf.readableBytes())
        output.writeBytes(buf)
    }

    override fun decode(ctx: ChannelHandlerContext, buf: ByteBuf, output: MutableList<Any>) {
        while (true) {
            val length = readableMessage(buf)
            if (length == -1)
                break
            output.add(buf.readRetainedSlice(length))
        }
    }

    /**
     * Reads the length and checks if the message can be read in one call.
     *
     * @param buf The byte buffer
     * @return The message length, or -1 if it's not possible to read a message
     */
    private fun readableMessage(buf: ByteBuf): Int {
        val index = buf.readerIndex()
        var bits = 0
        var length = 0
        var b: Byte
        do {
            // The variable integer is not complete, try again next time
            if (buf.readableBytes() < 1) {
                buf.readerIndex(index)
                return -1
            }
            b = buf.readByte()
            length = length or (b.toInt() and 0x7F shl bits)
            bits += 7
            if (bits > 35)
                throw DecoderException("Variable length is too long!")
        } while (b.toInt() and 0x80 != 0)
        if (length < 0)
            throw DecoderException("Message length cannot be negative: $length")
        // Not all the message bytes are available yet, try again later
        if (buf.readableBytes() < length) {
            buf.readerIndex(index)
            return -1
        }
        return length
    }
}
