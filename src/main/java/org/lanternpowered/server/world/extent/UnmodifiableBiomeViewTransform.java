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
import org.spongepowered.api.world.extent.UnmodifiableBiomeVolume;
import org.spongepowered.api.world.extent.worker.BiomeVolumeWorker;
import org.spongepowered.math.vector.Vector3i;

public class UnmodifiableBiomeViewTransform extends AbstractBiomeViewTransform<UnmodifiableBiomeVolume> implements UnmodifiableBiomeVolume {

    public UnmodifiableBiomeViewTransform(UnmodifiableBiomeVolume volume, DiscreteTransform3 transform) {
        super(volume, transform);
    }

    @Override
    public UnmodifiableBiomeVolume getBiomeView(Vector3i newMin, Vector3i newMax) {
        return new UnmodifiableBiomeViewDownsize(this.volume, this.inverseTransform.transform(newMin),
                this.inverseTransform.transform(newMax)).getBiomeView(this.transform);
    }

    @Override
    public UnmodifiableBiomeVolume getBiomeView(DiscreteTransform3 transform) {
        return new UnmodifiableBiomeViewTransform(this.volume, this.transform.withTransformation(transform));
    }

    @Override
    public BiomeVolumeWorker<? extends UnmodifiableBiomeVolume> getBiomeWorker() {
        return new LanternBiomeVolumeWorker<>(this);
    }
}
