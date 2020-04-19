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
package org.lanternpowered.server.util.gen.block;

import org.lanternpowered.server.world.extent.MutableBlockViewDownsize;
import org.lanternpowered.server.world.extent.MutableBlockViewTransform;
import org.lanternpowered.server.world.extent.UnmodifiableBlockVolumeWrapper;
import org.lanternpowered.server.world.extent.worker.LanternMutableBlockVolumeWorker;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.UnmodifiableBlockVolume;
import org.spongepowered.api.world.extent.worker.MutableBlockVolumeWorker;
import org.spongepowered.math.vector.Vector3i;

public abstract class AbstractMutableBlockBuffer extends AbstractBlockBuffer implements MutableBlockVolume {

    protected AbstractMutableBlockBuffer(Vector3i start, Vector3i size) {
        super(start, size);
    }

    @Override
    public MutableBlockVolumeWorker<? extends MutableBlockVolume> getBlockWorker() {
        return new LanternMutableBlockVolumeWorker<>(this);
    }

    @Override
    public UnmodifiableBlockVolume getUnmodifiableBlockView() {
        return new UnmodifiableBlockVolumeWrapper(this);
    }

    @Override
    public boolean setBlockType(int x, int y, int z, BlockType type) {
        return setBlock(x, y, z, type.getDefaultState());
    }

    @Override
    public MutableBlockVolume getBlockView(Vector3i newMin, Vector3i newMax) {
        checkRange(newMin);
        checkRange(newMax);
        return new MutableBlockViewDownsize(this, newMin, newMax);
    }

    @Override
    public MutableBlockVolume getBlockView(DiscreteTransform3 transform) {
        return new MutableBlockViewTransform(this, transform);
    }
}
