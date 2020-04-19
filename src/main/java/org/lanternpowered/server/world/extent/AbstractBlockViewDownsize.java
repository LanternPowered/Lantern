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

import org.lanternpowered.server.util.VecHelper;
import org.lanternpowered.server.util.gen.block.AtomicIntArrayMutableBlockBuffer;
import org.lanternpowered.server.util.gen.block.IntArrayImmutableBlockBuffer;
import org.lanternpowered.server.util.gen.block.IntArrayMutableBlockBuffer;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.world.extent.BlockVolume;
import org.spongepowered.api.world.extent.ImmutableBlockVolume;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.math.vector.Vector3i;

public abstract class AbstractBlockViewDownsize<V extends BlockVolume> implements BlockVolume {

    protected final V volume;
    protected final Vector3i min;
    protected final Vector3i max;
    protected final Vector3i size;

    public AbstractBlockViewDownsize(V volume, Vector3i min, Vector3i max) {
        this.volume = volume;
        this.min = min;
        this.max = max;
        this.size = max.sub(min).add(Vector3i.ONE);
    }

    protected final void checkRange(Vector3i position) {
        checkRange(position.getX(), position.getY(), position.getZ());
    }

    protected final void checkRange(int x, int y, int z) {
        if (!VecHelper.inBounds(x, y, z, this.min, this.max)) {
            throw new PositionOutOfBoundsException(new Vector3i(x, y, z), this.min, this.max);
        }
    }

    @Override
    public Vector3i getBlockMin() {
        return this.min;
    }

    @Override
    public Vector3i getBlockMax() {
        return this.max;
    }

    @Override
    public Vector3i getBlockSize() {
        return this.size;
    }

    @Override
    public boolean containsBlock(int x, int y, int z) {
        return VecHelper.inBounds(x, y, z, this.min, this.max);
    }

    @Override
    public BlockType getBlockType(int x, int y, int z) {
        return getBlock(x, y, z).getType();
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        checkRange(x, y, z);
        return this.volume.getBlock(x, y, z);
    }

    @Override
    public MutableBlockVolume getBlockCopy(StorageType type) {
        switch (type) {
            case STANDARD:
                return new IntArrayMutableBlockBuffer(ExtentBufferHelper.copyToBlockArray(
                        this, this.min, this.max, this.size), this.min, this.size);
            case THREAD_SAFE:
                return new AtomicIntArrayMutableBlockBuffer(ExtentBufferHelper.copyToBlockArray(
                        this, this.min, this.max, this.size), this.min, this.size);
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    @Override
    public ImmutableBlockVolume getImmutableBlockCopy() {
        return IntArrayImmutableBlockBuffer.newWithoutArrayClone(ExtentBufferHelper.copyToBlockArray(
                this, this.min, this.max, this.size), this.min, this.size);
    }
}
