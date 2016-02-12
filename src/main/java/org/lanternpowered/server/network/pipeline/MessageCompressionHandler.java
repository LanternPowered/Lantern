/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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

import static org.lanternpowered.server.network.message.codec.serializer.SimpleSerializerContext.DEFAULT;

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
    protected void encode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        ByteBuf prefixBuf = ctx.alloc().buffer(5);
        ByteBuf contentsBuf;

        if (msg.readableBytes() >= this.compressionThreshold) {
            // Message should be compressed
            int index = msg.readerIndex();
            int length = msg.readableBytes();

            byte[] sourceData = new byte[length];
            msg.readBytes(sourceData);
            deflater.setInput(sourceData);
            deflater.finish();

            byte[] compressedData = new byte[length];
            int compressedLength = deflater.deflate(compressedData);
            deflater.reset();

            if (compressedLength == 0) {
                // Compression failed in some weird way
                throw new EncoderException("Failed to compress message of size " + length);
            } else if (compressedLength >= length) {
                // Compression increased the size. threshold is probably too low
                // Send as an uncompressed packet
                DEFAULT.writeVarInt(prefixBuf, 0);
                msg.readerIndex(index);
                msg.retain();
                contentsBuf = msg;
            } else {
                // All is well
                DEFAULT.writeVarInt(prefixBuf, length);
                contentsBuf = Unpooled.wrappedBuffer(compressedData, 0, compressedLength);
            }
        } else {
            // Message should be sent through
            DEFAULT.writeVarInt(prefixBuf, 0);
            msg.retain();
            contentsBuf = msg;
        }

        out.add(Unpooled.wrappedBuffer(prefixBuf, contentsBuf));
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf msg, List<Object> out) throws Exception {
        int index = msg.readerIndex();
        int uncompressedSize = DEFAULT.readVarInt(msg);
        if (uncompressedSize == 0) {
            // Message is uncompressed
            int length = msg.readableBytes();
            if (length >= this.compressionThreshold) {
                // Invalid
                throw new DecoderException("Received uncompressed message of size " + length + " greater than threshold "
                        + this.compressionThreshold);
            }

            ByteBuf buf = ctx.alloc().buffer(length);
            msg.readBytes(buf, length);
            out.add(buf);
        } else {
            // Message is compressed
            byte[] sourceData = new byte[msg.readableBytes()];
            msg.readBytes(sourceData);
            this.inflater.setInput(sourceData);

            byte[] destData = new byte[uncompressedSize];
            int resultLength = this.inflater.inflate(destData);
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
