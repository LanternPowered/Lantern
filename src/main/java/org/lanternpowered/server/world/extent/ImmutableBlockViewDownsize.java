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
import org.spongepowered.api.world.extent.ImmutableBlockVolume;
import org.spongepowered.api.world.extent.worker.BlockVolumeWorker;
import org.spongepowered.math.vector.Vector3i;

public class ImmutableBlockViewDownsize extends AbstractBlockViewDownsize<ImmutableBlockVolume> implements ImmutableBlockVolume {

    public ImmutableBlockViewDownsize(ImmutableBlockVolume volume, Vector3i min, Vector3i max) {
        super(volume, min, max);
    }

    @Override
    public ImmutableBlockVolume getBlockView(Vector3i newMin, Vector3i newMax) {
        checkRange(newMin);
        checkRange(newMax);
        return new ImmutableBlockViewDownsize(this.volume, newMin, newMax);
    }

    @Override
    public ImmutableBlockVolume getBlockView(DiscreteTransform3 transform) {
        return new ImmutableBlockViewTransform(this, transform);
    }

    @Override
    public BlockVolumeWorker<? extends ImmutableBlockVolume> getBlockWorker() {
        return new LanternBlockVolumeWorker<>(this);
    }
}
