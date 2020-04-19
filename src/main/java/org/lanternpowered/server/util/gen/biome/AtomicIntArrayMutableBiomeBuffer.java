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
import org.lanternpowered.server.util.collect.array.concurrent.AtomicIntegerArrayHelper;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.extent.ImmutableBiomeVolume;
import org.spongepowered.api.world.extent.MutableBiomeVolume;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.math.vector.Vector3i;

import java.util.concurrent.atomic.AtomicIntegerArray;

/**
 * Mutable biome volume backed by a atomic short array.
 */
public final class AtomicIntArrayMutableBiomeBuffer extends AbstractMutableBiomeBuffer {

    private final AtomicIntegerArray biomes;

    public AtomicIntArrayMutableBiomeBuffer(Vector3i start, Vector3i size) {
        super(start, size);
        this.biomes = new AtomicIntegerArray(size.getX() * size.getY() * size.getZ());
    }

    public AtomicIntArrayMutableBiomeBuffer(int[] biomes, Vector3i start, Vector3i size) {
        super(start, size);
        this.biomes = new AtomicIntegerArray(biomes);
    }

    @Override
    public void setBiome(int x, int y, int z, BiomeType biome) {
        checkRange(x, y, z);
        this.biomes.set(index(x, y, z), BiomeRegistryModule.get().getInternalId(biome));
    }

    @Override
    public BiomeType getBiome(int x, int y, int z) {
        checkRange(x, y, z);
        return BiomeRegistryModule.get().getByInternalId(this.biomes.get(index(x, y, z))).orElse(BiomeTypes.OCEAN);
    }

    @Override
    public MutableBiomeVolume getBiomeCopy(StorageType type) {
        final int[] intArray = AtomicIntegerArrayHelper.toArray(this.biomes);
        switch (type) {
            case STANDARD:
                return new IntArrayMutableBiomeBuffer(intArray, this.start, this.size);
            case THREAD_SAFE:
                return new AtomicIntArrayMutableBiomeBuffer(intArray, this.start, this.size);
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    @Override
    public ImmutableBiomeVolume getImmutableBiomeCopy() {
        return IntArrayImmutableBiomeBuffer.newWithoutArrayClone(AtomicIntegerArrayHelper.toArray(this.biomes), this.start, this.size);
    }
}
