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
import org.spongepowered.api.world.extent.ImmutableBiomeVolume;
import org.spongepowered.api.world.extent.worker.BiomeVolumeWorker;
import org.spongepowered.math.vector.Vector3i;

public class ImmutableBiomeViewDownsize extends AbstractBiomeViewDownsize<ImmutableBiomeVolume> implements ImmutableBiomeVolume {

    public ImmutableBiomeViewDownsize(ImmutableBiomeVolume volume, Vector3i min, Vector3i max) {
        super(volume, min, max);
    }

    @Override
    public ImmutableBiomeVolume getBiomeView(Vector3i newMin, Vector3i newMax) {
        checkRange(newMin);
        checkRange(newMax);
        return new ImmutableBiomeViewDownsize(this.volume, newMin, newMax);
    }

    @Override
    public ImmutableBiomeVolume getBiomeView(DiscreteTransform3 transform) {
        return new ImmutableBiomeViewTransform(this, transform);
    }

    @Override
    public BiomeVolumeWorker<? extends ImmutableBiomeVolume> getBiomeWorker() {
        return new LanternBiomeVolumeWorker<>(this);
    }
}
