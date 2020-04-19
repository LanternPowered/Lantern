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
package org.lanternpowered.server.network.pipeline;

import static org.lanternpowered.server.network.buffer.LanternByteBuffer.readVarInt;
import static org.lanternpowered.server.network.buffer.LanternByteBuffer.writeVarInt;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import io.netty.handler.codec.MessageToMessageCodec;

import java.util.List;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

public final class MessageCompressionHandler extends MessageToMessageCodec<ByteBuf, ByteBuf> {

    private final Deflater deflater = new Deflater(Deflater.DEFAULT_COMPRESSION);
    private final Inflater inflater = new Inflater();

    private final int compressionThreshold;

    public MessageCompressionHandler(int compressionThreshold) {
        this.compressionThreshold = compressionThreshold;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) {
        final ByteBuf prefixBuf = ctx.alloc().buffer(5);
        final ByteBuf contentsBuf;

        if (msg.readableBytes() >= this.compressionThreshold) {
            // Message should be compressed
            final int index = msg.readerIndex();
            final int length = msg.readableBytes();

            final byte[] sourceData = new byte[length];
            msg.readBytes(sourceData);
            this.deflater.setInput(sourceData);
            this.deflater.finish();

            final byte[] compressedData = new byte[length];
            final int compressedLength = this.deflater.deflate(compressedData);
            this.deflater.reset();

            if (compressedLength == 0) {
                // Compression failed in some weird way
                throw new EncoderException("Failed to compress message of size " + length);
            } else if (compressedLength >= length) {
                // Compression increased the size. threshold is probably too low
                // Send as an uncompressed packet
                writeVarInt(prefixBuf, 0);
                msg.readerIndex(index);
                msg.retain();
                contentsBuf = msg;
            } else {
                // All is well
                writeVarInt(prefixBuf, length);
                contentsBuf = Unpooled.wrappedBuffer(compressedData, 0, compressedLength);
            }
        } else {
            // Message should be sent through
            writeVarInt(prefixBuf, 0);
            msg.retain();
            contentsBuf = msg;
        }

        out.add(Unpooled.wrappedBuffer(prefixBuf, contentsBuf));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        final int index = msg.readerIndex();
        final int uncompressedSize = readVarInt(msg);
        if (uncompressedSize == 0) {
            // Message is uncompressed
            final int length = msg.readableBytes();
            if (length >= this.compressionThreshold) {
                throw new DecoderException(String.format("Received uncompressed message of size %s greater than threshold %s",
                        length, this.compressionThreshold));
            }

            final ByteBuf buf = msg.slice();
            buf.retain(); // Retain the sliced buffer, otherwise will MessageToMessageCodec clean it up
            out.add(buf);
        } else {
            // Message is compressed
            final byte[] sourceData = new byte[msg.readableBytes()];
            msg.readBytes(sourceData);
            this.inflater.setInput(sourceData);

            final byte[] destData = new byte[uncompressedSize];
            final int resultLength = this.inflater.inflate(destData);
            this.inflater.reset();

            if (resultLength == 0) {
                // Might be a leftover from before compression was enabled (no compression header)
                // UncompressedSize is likely to be < threshold
                msg.readerIndex(index);
                msg.retain();
                out.add(msg);
            } else if (resultLength != uncompressedSize) {
                throw new DecoderException("Received compressed message claiming to be of size "
                        + uncompressedSize + " but actually " + resultLength);
            } else {
                out.add(Unpooled.wrappedBuffer(destData));
            }
        }
    }
}
