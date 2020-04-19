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

public class ImmutableBiomeViewTransform extends AbstractBiomeViewTransform<ImmutableBiomeVolume> implements ImmutableBiomeVolume {

    public ImmutableBiomeViewTransform(ImmutableBiomeVolume area, DiscreteTransform3 transform) {
        super(area, transform);
    }

    @Override
    public ImmutableBiomeVolume getBiomeView(Vector3i newMin, Vector3i newMax) {
        return new ImmutableBiomeViewDownsize(this.volume, this.inverseTransform.transform(newMin),
                this.inverseTransform.transform(newMax)).getBiomeView(this.transform);
    }

    @Override
    public ImmutableBiomeVolume getBiomeView(DiscreteTransform3 transform) {
        return new ImmutableBiomeViewTransform(this.volume, this.transform.withTransformation(transform));
    }

    @Override
    public BiomeVolumeWorker<? extends ImmutableBiomeVolume> getBiomeWorker() {
        return new LanternBiomeVolumeWorker<>(this);
    }
}
