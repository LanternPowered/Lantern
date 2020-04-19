/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.util.gen.biome;

import org.lanternpowered.server.game.registry.type.world.biome.BiomeRegistryModule;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.extent.ImmutableBiomeVolume;
import org.spongepowered.api.world.extent.MutableBiomeVolume;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.math.vector.Vector3i;

/**
 * Immutable biome volume, backed by a int array. The array passed to the
 * constructor is copied to ensure that the instance is immutable.
 */
public final class IntArrayImmutableBiomeBuffer extends AbstractImmutableBiomeBuffer {

    private final int[] biomes;

    public IntArrayImmutableBiomeBuffer(int[] biomes, Vector3i start, Vector3i size) {
        super(start, size);
        this.biomes = biomes.clone();
    }

    private IntArrayImmutableBiomeBuffer(Vector3i start, Vector3i size, int[] biomes) {
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
                return new IntArrayMutableBiomeBuffer(this.biomes.clone(), this.start, this.size);
            case THREAD_SAFE:
                return new AtomicIntArrayMutableBiomeBuffer(this.biomes, this.start, this.size);
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
    public static ImmutableBiomeVolume newWithoutArrayClone(int[] biomes, Vector3i start, Vector3i size) {
        return new IntArrayImmutableBiomeBuffer(start, size, biomes);
    }
}
