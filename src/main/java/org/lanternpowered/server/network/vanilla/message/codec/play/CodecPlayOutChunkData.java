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
package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.handler.codec.CodecException;
import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import org.lanternpowered.server.data.persistence.MemoryDataContainer;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutChunkData;
import org.lanternpowered.server.util.collect.array.VariableValueArray;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;

public final class CodecPlayOutChunkData implements Codec<MessagePlayOutChunkData> {

    private final static DataQuery X = DataQuery.of("x");
    private final static DataQuery Y = DataQuery.of("y");
    private final static DataQuery Z = DataQuery.of("z");

    @SuppressWarnings("ConstantConditions")
    @Override
    public ByteBuffer encode(CodecContext context, MessagePlayOutChunkData message) throws CodecException {
        final MessagePlayOutChunkData.Section[] sections = message.getSections();
        final int[] biomes = message.getBiomes();
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
            dataBuf.writeShort((short) section.getNonAirBlockCount());
            dataBuf.writeByte((byte) types.getBitsPerValue());
            final int[] palette = section.getPalette();
            if (palette != null) {
                dataBuf.writeVarInt(palette.length);
                for (int value : palette) {
                    dataBuf.writeVarInt(value);
                }
            }
            final long[] backing = types.getBacking();
            dataBuf.writeVarInt(backing.length);
            dataBuf.ensureWritable(backing.length * Long.BYTES);
            for (long value : backing) {
                dataBuf.writeLong(value);
            }
            final Short2ObjectMap<DataView> tileEntities = section.getTileEntities();
            if (!tileEntities.isEmpty() && tileEntitiesBuf == null) {
                tileEntitiesBuf = context.byteBufAlloc().buffer();
            }
            for (Short2ObjectMap.Entry<DataView> tileEntityEntry : tileEntities.short2ObjectEntrySet()) {
                tileEntitiesCount++;
                final int index = tileEntityEntry.getShortKey() & 0xffff;
                final DataView dataView = tileEntityEntry.getValue();
                dataView.set(X, x * 16 + (index & 0xf));
                dataView.set(Y, i << 4 | index >> 8);
                dataView.set(Z, z * 16 + ((index >> 4) & 0xf));
                tileEntitiesBuf.writeDataView(dataView);
            }
        }

        if (biomes != null) {
            dataBuf.ensureWritable(biomes.length * Integer.BYTES);
            for (int value : biomes) {
                dataBuf.writeInteger(value);
            }
        }

        buf.writeVarInt(sectionBitmask);
        buf.writeDataView(new MemoryDataContainer());
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
