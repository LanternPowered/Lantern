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
package org.lanternpowered.server.world.extent;

import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.util.gen.biome.AtomicObjectArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.biome.ObjectArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.block.AtomicIntArrayMutableBlockBuffer;
import org.lanternpowered.server.util.gen.block.IntArrayMutableBlockBuffer;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.extent.ArchetypeVolume;
import org.spongepowered.api.world.extent.ExtentBufferFactory;
import org.spongepowered.api.world.extent.MutableBiomeVolume;
import org.spongepowered.api.world.extent.MutableBlockVolume;

import java.util.Arrays;

public final class LanternExtentBufferFactory implements ExtentBufferFactory {

    public static final LanternExtentBufferFactory INSTANCE = new LanternExtentBufferFactory();

    private LanternExtentBufferFactory() {
    }

    @Override
    public MutableBiomeVolume createBiomeBuffer(Vector3i min, Vector3i size) {
        final BiomeType[] array = new BiomeType[size.getX() * size.getY() * size.getZ()];
        Arrays.fill(array, BiomeTypes.OCEAN);
        return new ObjectArrayMutableBiomeBuffer(array, min, size);
    }

    @Override
    public MutableBiomeVolume createThreadSafeBiomeBuffer(Vector3i min, Vector3i size) {
        final BiomeType[] array = new BiomeType[size.getX() * size.getY() * size.getZ()];
        Arrays.fill(array, BiomeTypes.OCEAN);
        return new AtomicObjectArrayMutableBiomeBuffer(array, min, size);
    }

    @Override
    public MutableBlockVolume createBlockBuffer(Vector3i min, Vector3i size) {
        return new IntArrayMutableBlockBuffer(min, size);
    }

    @Override
    public MutableBlockVolume createThreadSafeBlockBuffer(Vector3i min, Vector3i size) {
        return new AtomicIntArrayMutableBlockBuffer(min, size);
    }

    @Override
    public ArchetypeVolume createArchetypeVolume(Vector3i size, Vector3i origin) {
        return null;
    }
}
