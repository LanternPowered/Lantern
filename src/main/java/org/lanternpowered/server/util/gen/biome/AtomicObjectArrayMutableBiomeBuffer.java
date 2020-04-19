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

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.ImmutableBiomeVolume;
import org.spongepowered.api.world.extent.MutableBiomeVolume;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.math.vector.Vector3i;

import java.util.concurrent.atomic.AtomicReferenceArray;

/**
 * Mutable view of a {@link BiomeType} array.
 *
 * <p>Normally, the {@link AtomicIntArrayMutableBiomeBuffer} class uses memory more
 * efficiently, but when the {@link BiomeType} array is already created (for
 * example for a contract specified by Minecraft) this implementation becomes
 * more efficient.</p>
 */
public final class AtomicObjectArrayMutableBiomeBuffer extends AbstractMutableBiomeBuffer {

    private final AtomicReferenceArray<BiomeType> biomes;

    /**
     * Creates a new instance.
     *
     * @param biomes The biome array
     * @param start The start position
     * @param size The size
     */
    public AtomicObjectArrayMutableBiomeBuffer(BiomeType[] biomes, Vector3i start, Vector3i size) {
        super(start, size);
        this.biomes = new AtomicReferenceArray<>(biomes);
    }

    /**
     * Creates a new instance.
     *
     * @param biomes The biome array. The array is not copied, so changes made
     *        by this object will write through.
     * @param start The start position
     * @param size The size
     */
    private AtomicObjectArrayMutableBiomeBuffer(AtomicReferenceArray<BiomeType> biomes, Vector3i start, Vector3i size) {
        super(start, size);
        this.biomes = biomes;
    }

    @Override
    public BiomeType getBiome(int x, int y, int z) {
        checkRange(x, y, z);
        return this.biomes.get(index(x, y, z));
    }

    @Override
    public MutableBiomeVolume getBiomeCopy(StorageType type) {
        switch (type) {
            case STANDARD:
                final BiomeType[] array = new BiomeType[this.biomes.length()];
                for (int i = 0; i < array.length; i++) {
                    array[i] = this.biomes.get(i);
                }
                return new ObjectArrayMutableBiomeBuffer(array, this.start, this.size);
            case THREAD_SAFE:
                final AtomicReferenceArray<BiomeType> copy = new AtomicReferenceArray<>(this.biomes.length());
                for (int i = 0; i < copy.length(); i++) {
                    copy.set(i, this.biomes.get(i));
                }
                return new AtomicObjectArrayMutableBiomeBuffer(copy, this.start, this.size);
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    @Override
    public ImmutableBiomeVolume getImmutableBiomeCopy() {
        final BiomeType[] array = new BiomeType[this.biomes.length()];
        for (int i = 0; i < array.length; i++) {
            array[i] = this.biomes.get(i);
        }
        return new ObjectArrayImmutableBiomeBuffer(array, this.start, this.size);
    }

    @Override
    public void setBiome(int x, int y, int z, BiomeType biome) {
        checkNotNull(biome, "biome");
        checkRange(x, y, z);
        this.biomes.set(index(x, y, z), biome);
    }
}
