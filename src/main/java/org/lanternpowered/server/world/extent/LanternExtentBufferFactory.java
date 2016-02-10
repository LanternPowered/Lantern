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
package org.lanternpowered.server.world.extent;

import com.flowpowered.math.vector.Vector2i;
import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.util.gen.biome.AtomicObjectArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.biome.ObjectArrayMutableBiomeBuffer;
import org.lanternpowered.server.util.gen.block.AtomicShortArrayMutableBlockBuffer;
import org.lanternpowered.server.util.gen.block.ShortArrayMutableBlockBuffer;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.extent.ExtentBufferFactory;
import org.spongepowered.api.world.extent.MutableBiomeArea;
import org.spongepowered.api.world.extent.MutableBlockVolume;

import java.util.Arrays;

public final class LanternExtentBufferFactory implements ExtentBufferFactory {

    public static final LanternExtentBufferFactory INSTANCE = new LanternExtentBufferFactory();

    private LanternExtentBufferFactory() {
    }

    @Override
    public MutableBiomeArea createBiomeBuffer(Vector2i size) {
        final BiomeType[] array = new BiomeType[size.getX() * size.getY()];
        Arrays.fill(array, BiomeTypes.OCEAN);
        return new ObjectArrayMutableBiomeBuffer(array, Vector2i.ZERO, size);
    }

    @Override
    public MutableBiomeArea createBiomeBuffer(int xSize, int zSize) {
        return this.createBiomeBuffer(new Vector2i(xSize, zSize));
    }

    @Override
    public MutableBiomeArea createThreadSafeBiomeBuffer(Vector2i size) {
        final BiomeType[] array = new BiomeType[size.getX() * size.getY()];
        Arrays.fill(array, BiomeTypes.OCEAN);
        return new AtomicObjectArrayMutableBiomeBuffer(array, Vector2i.ZERO, size);
    }

    @Override
    public MutableBiomeArea createThreadSafeBiomeBuffer(int xSize, int zSize) {
        return this.createThreadSafeBiomeBuffer(new Vector2i(xSize, zSize));
    }

    @Override
    public MutableBlockVolume createBlockBuffer(Vector3i size) {
        return new ShortArrayMutableBlockBuffer(Vector3i.ZERO, size);
    }

    @Override
    public MutableBlockVolume createBlockBuffer(int xSize, int ySize, int zSize) {
        return this.createBlockBuffer(new Vector3i(xSize, ySize, zSize));
    }

    @Override
    public MutableBlockVolume createThreadSafeBlockBuffer(Vector3i size) {
        return new AtomicShortArrayMutableBlockBuffer(Vector3i.ZERO, size);
    }

    @Override
    public MutableBlockVolume createThreadSafeBlockBuffer(int xSize, int ySize, int zSize) {
        return this.createThreadSafeBlockBuffer(new Vector3i(xSize, ySize, zSize));
    }
}
