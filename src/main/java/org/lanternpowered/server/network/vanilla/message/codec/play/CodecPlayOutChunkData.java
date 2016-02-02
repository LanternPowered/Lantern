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
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutChunkData;
import org.lanternpowered.server.util.VariableValueArray;

public final class CodecPlayOutChunkData implements Codec<MessagePlayOutChunkData> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutChunkData message) throws CodecException {
        final MessagePlayOutChunkData.Section[] sections = message.getSections();
        final byte[] biomes = message.getBiomes();

        int sectionBitmask = 0;
        if (sections != null) {
            final int maxBitmask = (1 << sections.length) - 1;

            if (biomes != null) {
                sectionBitmask = maxBitmask;
            } else {
                sectionBitmask &= maxBitmask;
            }

            for (int i = 0; i < sections.length; ++i) {
                if (sections[i] == null) {
                    // Remove empty sections from bitmask
                    sectionBitmask &= ~(1 << i);
                }
            }
        }

        final ByteBuf buf = context.byteBufAlloc().buffer();
        buf.writeInt(message.getX());
        buf.writeInt(message.getZ());
        buf.writeBoolean(biomes != null);
        context.writeVarInt(buf, sectionBitmask);

        final ByteBuf dataBuf = context.byteBufAlloc().buffer();
        if (sections != null) {
            for (int i = 0, mask = 1; i < sections.length; ++i, mask <<= 1) {
                if ((sectionBitmask & mask) != 0) {
                    MessagePlayOutChunkData.Section section = sections[i];
                    VariableValueArray types = section.getTypes();
                    dataBuf.writeByte(types.getBitsPerValue());
                    int[] palette = section.getPalette();
                    if (palette != null) {
                        context.writeVarInt(dataBuf, palette.length);
                        for (int value : palette) {
                            context.writeVarInt(dataBuf, value);
                        }
                    } else {
                        // Using global palette
                        context.writeVarInt(dataBuf, 0);
                    }
                    long[] backing = types.getBacking();
                    context.writeVarInt(dataBuf, backing.length);
                    byte[] blockLight = section.getBlockLight();
                    byte[] skyLightData = section.getSkyLight();
                    dataBuf.ensureWritable(backing.length * 8 + blockLight.length +
                            (skyLightData != null ? skyLightData.length : 0));
                    for (long value : backing) {
                        dataBuf.writeLong(value);
                    }
                    dataBuf.writeBytes(blockLight);
                    if (skyLightData != null) {
                        dataBuf.writeBytes(skyLightData);
                    }
                }
            }
        }

        if (biomes != null) {
            dataBuf.writeBytes(biomes);
        }

        context.writeVarInt(buf, dataBuf.writerIndex());
        try {
            buf.writeBytes(dataBuf);
        } finally {
            dataBuf.release();
        }

        return buf;
    }

}
