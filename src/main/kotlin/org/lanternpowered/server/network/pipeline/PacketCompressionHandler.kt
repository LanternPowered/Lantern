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
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.DecoderException
import io.netty.handler.codec.EncoderException
import io.netty.handler.codec.MessageToMessageCodec
import org.lanternpowered.server.network.buffer.LanternByteBuffer
import java.util.zip.Deflater
import java.util.zip.Inflater

class PacketCompressionHandler(
        private val compressionThreshold: Int
) : MessageToMessageCodec<ByteBuf, ByteBuf>() {

    private val deflater = Deflater(Deflater.DEFAULT_COMPRESSION)
    private val inflater = Inflater()

    override fun encode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        val prefixBuf = ctx.alloc().buffer(5)
        val contentsBuf = if (msg.readableBytes() >= this.compressionThreshold) {
            // Message should be compressed
            val index = msg.readerIndex()
            val length = msg.readableBytes()
            val sourceData = ByteArray(length)
            msg.readBytes(sourceData)
            this.deflater.setInput(sourceData)
            this.deflater.finish()
            val compressedData = ByteArray(length)
            val compressedLength = this.deflater.deflate(compressedData)
            this.deflater.reset()
            when {
                // Compression failed in some weird way
                compressedLength == 0 -> throw EncoderException("Failed to compress message of size $length")
                compressedLength >= length -> {
                    // Compression increased the size. threshold is probably too low
                    // Send as an uncompressed packet
                    LanternByteBuffer.writeVarInt(prefixBuf, 0)
                    msg.readerIndex(index)
                    msg.retain()
                }
                else -> {
                    // All is well
                    LanternByteBuffer.writeVarInt(prefixBuf, length)
                    Unpooled.wrappedBuffer(compressedData, 0, compressedLength)
                }
            }
        } else {
            // Message should be sent through
            LanternByteBuffer.writeVarInt(prefixBuf, 0)
            msg.retain()
        }
        out.add(Unpooled.wrappedBuffer(prefixBuf, contentsBuf))
    }

    override fun decode(ctx: ChannelHandlerContext, msg: ByteBuf, out: MutableList<Any>) {
        val index = msg.readerIndex()
        val uncompressedSize = LanternByteBuffer.readVarInt(msg)
        if (uncompressedSize == 0) {
            // Message is uncompressed
            val length = msg.readableBytes()
            if (length >= this.compressionThreshold)
                throw DecoderException("Received uncompressed message of size $length greater than threshold $compressionThreshold")
            val buf = msg.slice()
            buf.retain() // Retain the sliced buffer, otherwise will MessageToMessageCodec clean it up
            out.add(buf)
        } else {
            // Message is compressed
            val sourceData = ByteArray(msg.readableBytes())
            msg.readBytes(sourceData)
            this.inflater.setInput(sourceData)
            val destData = ByteArray(uncompressedSize)
            val resultLength = this.inflater.inflate(destData)
            this.inflater.reset()
            when {
                resultLength == 0 -> {
                    // Might be a leftover from before compression was enabled (no compression header)
                    // uncompressedSize is likely to be < threshold
                    msg.readerIndex(index)
                    msg.retain()
                    out.add(msg)
                }
                resultLength != uncompressedSize -> throw DecoderException(
                        "Received compressed message claiming to be of size $uncompressedSize but actually $resultLength")
                else -> out.add(Unpooled.wrappedBuffer(destData))
            }
        }
    }

}