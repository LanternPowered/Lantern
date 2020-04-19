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

import org.lanternpowered.server.world.extent.worker.LanternMutableBlockVolumeWorker;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.UnmodifiableBlockVolume;
import org.spongepowered.api.world.extent.worker.MutableBlockVolumeWorker;
import org.spongepowered.math.vector.Vector3i;

public class MutableBlockViewDownsize extends AbstractBlockViewDownsize<MutableBlockVolume> implements MutableBlockVolume {

    public MutableBlockViewDownsize(MutableBlockVolume volume, Vector3i min, Vector3i max) {
        super(volume, min, max);
    }

    @Override
    public boolean setBlock(int x, int y, int z, BlockState block) {
        checkRange(x, y, z);
        return this.volume.setBlock(x, y, z, block);
    }

    @Override
    public MutableBlockVolume getBlockView(Vector3i newMin, Vector3i newMax) {
        checkRange(newMin);
        checkRange(newMax);
        return new MutableBlockViewDownsize(this.volume, newMin, newMax);
    }

    @Override
    public MutableBlockVolume getBlockView(DiscreteTransform3 transform) {
        return new MutableBlockViewTransform(this, transform);
    }

    @Override
    public MutableBlockVolumeWorker<? extends MutableBlockVolume> getBlockWorker() {
        return new LanternMutableBlockVolumeWorker<>(this);
    }

    @Override
    public UnmodifiableBlockVolume getUnmodifiableBlockView() {
        return new UnmodifiableBlockVolumeWrapper(this);
    }

}
