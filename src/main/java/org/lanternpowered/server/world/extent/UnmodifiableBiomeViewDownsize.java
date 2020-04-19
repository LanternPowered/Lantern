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
import org.spongepowered.api.world.extent.BiomeVolume;
import org.spongepowered.api.world.extent.UnmodifiableBiomeVolume;
import org.spongepowered.api.world.extent.worker.BiomeVolumeWorker;
import org.spongepowered.math.vector.Vector3i;

public class UnmodifiableBiomeViewDownsize extends AbstractBiomeViewDownsize<BiomeVolume> implements UnmodifiableBiomeVolume {

    public UnmodifiableBiomeViewDownsize(BiomeVolume volume, Vector3i min, Vector3i max) {
        super(volume, min, max);
    }

    @Override
    public UnmodifiableBiomeVolume getBiomeView(Vector3i newMin, Vector3i newMax) {
        checkRange(newMin);
        checkRange(newMax);
        return new UnmodifiableBiomeViewDownsize(this.volume, newMin, newMax);
    }

    @Override
    public UnmodifiableBiomeVolume getBiomeView(DiscreteTransform3 transform) {
        return new UnmodifiableBiomeViewTransform(this, transform);
    }

    @Override
    public BiomeVolumeWorker<? extends UnmodifiableBiomeVolume> getBiomeWorker() {
        return new LanternBiomeVolumeWorker<>(this);
    }
}
