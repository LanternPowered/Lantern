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

import org.lanternpowered.server.world.extent.worker.LanternBlockVolumeWorker;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.world.extent.UnmodifiableBlockVolume;
import org.spongepowered.api.world.extent.worker.BlockVolumeWorker;
import org.spongepowered.math.vector.Vector3i;

public class UnmodifiableBlockViewTransform extends AbstractBlockViewTransform<UnmodifiableBlockVolume> implements UnmodifiableBlockVolume {

    public UnmodifiableBlockViewTransform(UnmodifiableBlockVolume volume, DiscreteTransform3 transform) {
        super(volume, transform);
    }

    @Override
    public UnmodifiableBlockVolume getBlockView(Vector3i newMin, Vector3i newMax) {
        return new UnmodifiableBlockViewDownsize(this.volume, this.inverseTransform.transform(newMin),
                this.inverseTransform.transform(newMax)).getBlockView(this.transform);
    }

    @Override
    public UnmodifiableBlockVolume getBlockView(DiscreteTransform3 transform) {
        return new UnmodifiableBlockViewTransform(this.volume, this.transform.withTransformation(transform));
    }

    @Override
    public BlockVolumeWorker<? extends UnmodifiableBlockVolume> getBlockWorker() {
        return new LanternBlockVolumeWorker<>(this);
    }
}
