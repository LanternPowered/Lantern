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
package org.lanternpowered.server.network.vanilla.message.type.play;

import static com.google.common.base.Preconditions.checkNotNull;

import it.unimi.dsi.fastutil.shorts.Short2ObjectMap;
import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.util.collect.array.VariableValueArray;
import org.spongepowered.api.data.DataView;

import org.checkerframework.checker.nullness.qual.Nullable;

public final class MessagePlayOutChunkData implements Message {

    private final int x;
    private final int z;

    private final Section[] sections;
    @Nullable private final int[] biomes;

    public MessagePlayOutChunkData(int x, int z, Section[] sections, @Nullable int[] biomes) {
        checkNotNull(sections, "sections");
        this.sections = sections;
        this.biomes = biomes;
        this.x = x;
        this.z = z;
    }

    public Section[] getSections() {
        return this.sections;
    }

    public int getX() {
        return this.x;
    }

    public int getZ() {
        return this.z;
    }

    @Nullable
    public int[] getBiomes() {
        return this.biomes;
    }

    /**
     * Represents the data of chunk section.
     *
     * Notes:
     * - If bitsPerValue is smaller then 4 bits, the client will round up to 4
     * - When bitsPerValue is greater then 8 bits, the client will use the global palette
     */
    public static class Section {

        private final VariableValueArray types;
        @Nullable private final int[] palette;
        private final int nonAirBlockCount;
        private final Short2ObjectMap<DataView> tileEntities;

        public Section(VariableValueArray types, @Nullable int[] palette, int nonAirBlockCount, Short2ObjectMap<DataView> tileEntities) {
            this.nonAirBlockCount = nonAirBlockCount;
            this.tileEntities = tileEntities;
            this.palette = palette;
            this.types = types;
        }

        public int getNonAirBlockCount() {
            return this.nonAirBlockCount;
        }

        public VariableValueArray getTypes() {
            return this.types;
        }

        @Nullable
        public int[] getPalette() {
            return this.palette;
        }

        public Short2ObjectMap<DataView> getTileEntities() {
            return this.tileEntities;
        }
    }

}
