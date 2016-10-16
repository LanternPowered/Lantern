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
package org.lanternpowered.server.util.gen.biome;

import com.flowpowered.math.vector.Vector3i;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.MutableBiomeVolume;
import org.spongepowered.api.world.extent.StorageType;

/**
 * Mutable view of a {@link BiomeType} array.
 *
 * <p>Normally, the {@link ShortArrayMutableBiomeBuffer} class uses memory more
 * efficiently, but when the {@link BiomeType} array is already created (for
 * example for a contract specified by Minecraft) this implementation becomes
 * more efficient.</p>
 */
public final class ObjectArrayImmutableBiomeBuffer extends AbstractImmutableBiomeBuffer {

    private final BiomeType[] biomes;

    /**
     * Creates a new instance.
     *
     * @param biomes The biome array. The array is not copied, so changes made
     *        by this object will write through.
     * @param start The start position
     * @param size The size
     */
    public ObjectArrayImmutableBiomeBuffer(BiomeType[] biomes, Vector3i start, Vector3i size) {
        super(start, size);
        this.biomes = biomes.clone();
    }

    @Override
    public BiomeType getBiome(int x, int y, int z) {
        checkRange(x, y, z);
        return this.biomes[index(x, y, z)];
    }

    @Override
    public MutableBiomeVolume getBiomeCopy(StorageType type) {
        switch (type) {
            case STANDARD:
                return new ObjectArrayMutableBiomeBuffer(this.biomes.clone(), this.start, this.size);
            case THREAD_SAFE:
                return new AtomicObjectArrayMutableBiomeBuffer(this.biomes, this.start, this.size);
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }
}
