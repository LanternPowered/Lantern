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
import org.lanternpowered.server.game.registry.type.world.biome.BiomeRegistryModule;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.extent.ImmutableBiomeVolume;
import org.spongepowered.api.world.extent.MutableBiomeVolume;
import org.spongepowered.api.world.extent.StorageType;

/**
 * Immutable biome volume, backed by a short array. The array passed to the
 * constructor is copied to ensure that the instance is immutable.
 */
public final class ShortArrayImmutableBiomeBuffer extends AbstractImmutableBiomeBuffer {

    private final short[] biomes;

    public ShortArrayImmutableBiomeBuffer(short[] biomes, Vector3i start, Vector3i size) {
        super(start, size);
        this.biomes = biomes.clone();
    }

    private ShortArrayImmutableBiomeBuffer(Vector3i start, Vector3i size, short[] biomes) {
        super(start, size);
        this.biomes = biomes;
    }

    @Override
    public BiomeType getBiome(int x, int y, int z) {
        checkRange(x, y, z);
        return BiomeRegistryModule.get().getByInternalId(this.biomes[index(x, y, z)]).orElse(BiomeTypes.OCEAN);
    }

    @Override
    public MutableBiomeVolume getBiomeCopy(StorageType type) {
        switch (type) {
            case STANDARD:
                return new ShortArrayMutableBiomeBuffer(this.biomes.clone(), this.start, this.size);
            case THREAD_SAFE:
                return new AtomicShortArrayMutableBiomeBuffer(this.biomes, this.start, this.size);
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    /**
     * This method doesn't clone the array passed into it. INTERNAL USE ONLY.
     * Make sure your code doesn't leak the reference if you're using it.
     *
     * @param biomes The biomes to store
     * @param start The start of the volume
     * @param size The size of the volume
     * @return A new buffer using the same array reference
     */
    public static ImmutableBiomeVolume newWithoutArrayClone(short[] biomes, Vector3i start, Vector3i size) {
        return new ShortArrayImmutableBiomeBuffer(start, size, biomes);
    }
}
