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
package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.handler.codec.CodecException;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutChunkData;
import org.lanternpowered.server.util.VariableValueArray;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;

public final class CodecPlayOutChunkData implements Codec<MessagePlayOutChunkData> {

    private final static DataQuery X = DataQuery.of("x");
    private final static DataQuery Y = DataQuery.of("y");
    private final static DataQuery Z = DataQuery.of("z");

    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutChunkData message) throws CodecException {
        final MessagePlayOutChunkData.Section[] sections = message.getSections();
        final byte[] biomes = message.getBiomes();
        final int x = message.getX();
        final int z = message.getZ();

        final ByteBuffer buf = context.byteBufAlloc().buffer();
        buf.writeInteger(message.getX());
        buf.writeInteger(message.getZ());
        buf.writeBoolean(biomes != null);

        int sectionBitmask = 0;

        final ByteBuffer dataBuf = context.byteBufAlloc().buffer();

        ByteBuffer tileEntitiesBuf = null;
        int tileEntitiesCount = 0;

        for (int i = 0; i < sections.length; i++) {
            if (sections[i] == null) {
                continue;
            }
            sectionBitmask |= 1 << i;
            final MessagePlayOutChunkData.Section section = sections[i];
            final VariableValueArray types = section.getTypes();
            dataBuf.writeByte((byte) types.getBitsPerValue());
            final int[] palette = section.getPalette();
            if (palette != null) {
                dataBuf.writeVarInt(palette.length);
                for (int value : palette) {
                    dataBuf.writeVarInt(value);
                }
            } else {
                // Using global palette
                dataBuf.writeVarInt(0);
            }
            final long[] backing = types.getBacking();
            dataBuf.writeVarInt(backing.length);
            final byte[] blockLight = section.getBlockLight();
            final byte[] skyLight = section.getSkyLight();
            dataBuf.ensureWritable(backing.length * 8 + blockLight.length +
                    (skyLight != null ? skyLight.length : 0));
            for (long value : backing) {
                dataBuf.writeLong(value);
            }
            dataBuf.writeBytes(blockLight);
            if (skyLight != null) {
                dataBuf.writeBytes(skyLight);
            }
            final Short2ObjectMap<DataView> tileEntities = section.getTileEntities();
            if (!tileEntities.isEmpty() && tileEntitiesBuf == null) {
                tileEntitiesBuf  = context.byteBufAlloc().buffer();
            }
            for (Short2ObjectMap.Entry<DataView> tileEntityEntry : tileEntities.short2ObjectEntrySet()) {
                tileEntitiesCount++;
                final int index = tileEntityEntry.getShortKey() & 0xffff;
                final DataView dataView = tileEntityEntry.getValue();
                dataView.set(X, x * 16 + (index & 0xf));
                dataView.set(Y, i << 4 | index >> 8);
                dataView.set(Z, z * 16 + ((index >> 4) & 0xf));
                System.out.println(dataView);
                //noinspection ConstantConditions
                tileEntitiesBuf.writeDataView(dataView);
            }
        }

        if (biomes != null) {
            dataBuf.writeBytes(biomes);
        }

        buf.writeVarInt(sectionBitmask);
        buf.writeVarInt(dataBuf.writerIndex());
        try {
            buf.writeBytes(dataBuf);
        } finally {
            dataBuf.release();
        }

        buf.writeVarInt(tileEntitiesCount);
        if (tileEntitiesBuf != null) {
            try {
                buf.writeBytes(tileEntitiesBuf);
            } finally {
                tileEntitiesBuf.release();
            }
        }

        return buf;
    }

}
