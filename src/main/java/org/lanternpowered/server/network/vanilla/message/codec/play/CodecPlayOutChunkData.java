/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
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
package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutChunkData;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutUnloadChunk;
import org.lanternpowered.server.world.chunk.LanternChunk;

public final class CodecPlayOutChunkData implements Codec<Message> {

    @Override
    public ByteBuf encode(CodecContext context, Message message) throws CodecException {
        if (message instanceof MessagePlayOutUnloadChunk) {
            final MessagePlayOutUnloadChunk message1 = (MessagePlayOutUnloadChunk) message;
            return context.writeVarInt(context.byteBufAlloc().buffer()
                    .writeInt(message1.getX())
                    .writeInt(message1.getZ())
                    .writeBoolean(true)
                    .writeShort(0), 0);
        }

        final MessagePlayOutChunkData message1 = (MessagePlayOutChunkData) message;
        final MessagePlayOutChunkData.Section[] sections = message1.getSections();
        final byte[] biomes = message1.getBiomes();
        final boolean skylight = message1.hasSkyLight();

        int sectionCount;
        int sectionBitmask = 0;
        if (sections == null) {
            sectionCount = 0;
        } else {
            final int maxBitmask = (1 << sections.length) - 1;

            if (biomes != null) {
                sectionBitmask = maxBitmask;
                sectionCount = sections.length;
            } else {
                sectionBitmask &= maxBitmask;
                sectionCount = countBits(sectionBitmask);
            }

            for (int i = 0; i < sections.length; ++i) {
                if (sections[i] == null) {
                    // Remove empty sections from bitmask
                    sectionBitmask &= ~(1 << i);
                    sectionCount--;
                }
            }
        }

        // Calculate how big the data will need to be
        int byteSize = 0;

        if (sections != null) {
            final int numBlocks = LanternChunk.CHUNK_SECTION_VOLUME;
            int sectionSize = numBlocks * 5 / 2;  // (data and metadata combo) * 2 + blockLight / 2
            if (skylight) {
                sectionSize += numBlocks / 2;  // + skyLight/2
            }
            byteSize += sectionCount * sectionSize;
        }

        if (biomes != null) {
            byteSize += 256;  // + biomes
        }


        final ByteBuf buf = context.byteBufAlloc().buffer();
        buf.writeInt(message1.getX());
        buf.writeInt(message1.getZ());
        buf.writeBoolean(biomes != null);
        // context.writeVarInt(buf, sectionBitmask); 1.9
        buf.writeShort(sectionBitmask);
        context.writeVarInt(buf, byteSize);
        buf.ensureWritable(byteSize);

        if (sections != null) {
            // Get the list of sections
            MessagePlayOutChunkData.Section[] sendSections = new MessagePlayOutChunkData.Section[sectionCount];
            for (int i = 0, j = 0, mask = 1; i < sections.length; ++i, mask <<= 1) {
                if ((sectionBitmask & mask) != 0) {
                    sendSections[j++] = sections[i];
                }
            }

            // Block types
            for (MessagePlayOutChunkData.Section section : sendSections) {
                for (short type : section.getBlockTypes()) {
                    buf.writeShort(type);
                }
            }

            // Block light
            for (MessagePlayOutChunkData.Section section : sendSections) {
                buf.writeBytes(section.getBlockLight());
            }

            // Sky light
            if (skylight) {
                for (MessagePlayOutChunkData.Section section : sendSections) {
                    buf.writeBytes(section.getSkyLight());
                }
            }
        }

        if (biomes != null) {
            buf.writeBytes(biomes);
        }

        return buf;
    }

    @Override
    public Message decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }

    private static int countBits(int v) {
        // http://graphics.stanford.edu/~seander/bithacks.html#CountBitsSetKernighan
        int c;
        for (c = 0; v > 0; c++) {
            v &= v - 1;
        }
        return c;
    }

}
