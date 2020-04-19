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

import org.lanternpowered.server.world.extent.worker.LanternMutableBiomeVolumeWorker;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.MutableBiomeVolume;
import org.spongepowered.api.world.extent.UnmodifiableBiomeVolume;
import org.spongepowered.api.world.extent.worker.MutableBiomeVolumeWorker;
import org.spongepowered.math.vector.Vector3i;

public class MutableBiomeViewDownsize extends AbstractBiomeViewDownsize<MutableBiomeVolume> implements MutableBiomeVolume {

    public MutableBiomeViewDownsize(MutableBiomeVolume area, Vector3i min, Vector3i max) {
        super(area, min, max);
    }

    @Override
    public void setBiome(int x, int y, int z, BiomeType biome) {
        checkRange(x, y, z);
        this.volume.setBiome(x, y, z, biome);
    }

    @Override
    public MutableBiomeVolume getBiomeView(Vector3i newMin, Vector3i newMax) {
        checkRange(newMin);
        checkRange(newMax);
        return new MutableBiomeViewDownsize(this.volume, newMin, newMax);
    }

    @Override
    public MutableBiomeVolume getBiomeView(DiscreteTransform3 transform) {
        return new MutableBiomeViewTransform(this, transform);
    }

    @Override
    public MutableBiomeVolumeWorker<? extends MutableBiomeVolume> getBiomeWorker() {
        return new LanternMutableBiomeVolumeWorker<>(this);
    }

    @Override
    public UnmodifiableBiomeVolume getUnmodifiableBiomeView() {
        return new UnmodifiableBiomeVolumeWrapper(this);
    }

}
