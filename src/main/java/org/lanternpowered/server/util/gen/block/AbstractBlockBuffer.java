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

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.util.VecHelper;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.util.PositionOutOfBoundsException;
import org.spongepowered.api.world.extent.BlockVolume;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.math.vector.Vector3i;

/**
 * Base class for block buffers.
 */
public abstract class AbstractBlockBuffer implements BlockVolume {

    protected Vector3i start;
    protected Vector3i size;
    protected Vector3i end;

    private final int yLine;
    private final int yzSlice;

    protected AbstractBlockBuffer(Vector3i start, Vector3i size) {
        this.end = start.add(size).sub(Vector3i.ONE);
        this.start = start;
        this.size = size;
        this.yLine = size.getY();
        this.yzSlice = this.yLine * size.getZ();
    }

    protected void checkRange(Vector3i position) {
        checkRange(position.getX(), position.getY(), position.getZ());
    }

    protected void checkRange(int x, int y, int z) {
        if (!VecHelper.inBounds(x, y, z, this.start, this.end)) {
            throw new PositionOutOfBoundsException(new Vector3i(x, y, z), this.start, this.end);
        }
    }

    protected int index(int x, int y, int z) {
        return (x - this.start.getX()) * this.yzSlice + (z - this.start.getZ()) * this.yLine + (y - this.start.getY());
    }

    @Override
    public Vector3i getBlockMax() {
        return this.end;
    }

    @Override
    public Vector3i getBlockMin() {
        return this.start;
    }

    @Override
    public Vector3i getBlockSize() {
        return this.size;
    }

    @Override
    public boolean containsBlock(Vector3i position) {
        return containsBlock(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public boolean containsBlock(int x, int y, int z) {
        return VecHelper.inBounds(x, y, z, this.start, this.end);
    }

    @Override
    public BlockState getBlock(Vector3i position) {
        return getBlock(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public BlockType getBlockType(Vector3i position) {
        return getBlockType(position.getX(), position.getY(), position.getZ());
    }

    @Override
    public BlockType getBlockType(int x, int y, int z) {
        return getBlock(x, y, z).getType();
    }

    @Override
    public MutableBlockVolume getBlockCopy() {
        return getBlockCopy(StorageType.STANDARD);
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
            .add("min", this.getBlockMin())
            .add("max", this.getBlockMax())
            .toString();
    }
}
