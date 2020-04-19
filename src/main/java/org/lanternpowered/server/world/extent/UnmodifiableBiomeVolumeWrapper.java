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
package org.lanternpowered.server.world.extent;

import org.lanternpowered.server.world.extent.worker.LanternBiomeVolumeWorker;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.ImmutableBiomeVolume;
import org.spongepowered.api.world.extent.MutableBiomeVolume;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.api.world.extent.UnmodifiableBiomeVolume;
import org.spongepowered.api.world.extent.worker.BiomeVolumeWorker;
import org.spongepowered.math.vector.Vector3i;

public class UnmodifiableBiomeVolumeWrapper implements UnmodifiableBiomeVolume {

    private final MutableBiomeVolume volume;

    public UnmodifiableBiomeVolumeWrapper(MutableBiomeVolume volume) {
        this.volume = volume;
    }

    @Override
    public Vector3i getBiomeMin() {
        return this.volume.getBiomeMin();
    }

    @Override
    public Vector3i getBiomeMax() {
        return this.volume.getBiomeMax();
    }

    @Override
    public Vector3i getBiomeSize() {
        return this.volume.getBiomeSize();
    }

    @Override
    public boolean containsBiome(int x, int y, int z) {
        return this.volume.containsBiome(x, y, z);
    }

    @Override
    public BiomeType getBiome(int x, int y, int z) {
        return this.volume.getBiome(x, y, z);
    }

    @Override
    public UnmodifiableBiomeVolume getBiomeView(Vector3i newMin, Vector3i newMax) {
        return new UnmodifiableBiomeVolumeWrapper(this.volume.getBiomeView(newMin, newMax));
    }

    @Override
    public UnmodifiableBiomeVolume getBiomeView(DiscreteTransform3 transform) {
        return new UnmodifiableBiomeVolumeWrapper(this.volume.getBiomeView(transform));
    }

    @Override
    public BiomeVolumeWorker<? extends UnmodifiableBiomeVolume> getBiomeWorker() {
        return new LanternBiomeVolumeWorker<>(this);
    }

    @Override
    public MutableBiomeVolume getBiomeCopy(StorageType type) {
        return this.volume.getBiomeCopy(type);
    }

    @Override
    public ImmutableBiomeVolume getImmutableBiomeCopy() {
        return this.volume.getImmutableBiomeCopy();
    }
}
