/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.util.gen.concurrent;

import com.flowpowered.math.vector.Vector3i;

import org.lanternpowered.server.block.LanternBlocks;
import org.lanternpowered.server.util.concurrent.AtomicShortArray;
import org.lanternpowered.server.util.gen.AbstractBlockBuffer;
import org.lanternpowered.server.util.gen.ShortArrayImmutableBlockBuffer;
import org.lanternpowered.server.util.gen.ShortArrayMutableBlockBuffer;
import org.lanternpowered.server.world.extent.MutableBlockViewDownsize;
import org.lanternpowered.server.world.extent.MutableBlockViewTransform;
import org.lanternpowered.server.world.extent.UnmodifiableBlockVolumeWrapper;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.util.DiscreteTransform3;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.extent.ImmutableBlockVolume;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.api.world.extent.UnmodifiableBlockVolume;

@NonnullByDefault
public class AtomicShortArrayMutableBlockBuffer extends AbstractBlockBuffer implements MutableBlockVolume {

    private final BlockState air = BlockTypes.AIR.getDefaultState();
    private final AtomicShortArray blocks;

    public AtomicShortArrayMutableBlockBuffer(Vector3i start, Vector3i size) {
        super(start, size);
        this.blocks = new AtomicShortArray(size.getX() * size.getY() * size.getZ());
    }

    public AtomicShortArrayMutableBlockBuffer(short[] blocks, Vector3i start, Vector3i size) {
        super(start, size);
        this.blocks = new AtomicShortArray(blocks);
    }

    @Override
    public void setBlock(Vector3i position, BlockState block) {
        this.setBlock(position.getX(), position.getY(), position.getZ(), block);
    }

    @Override
    public void setBlockType(Vector3i position, BlockType type) {
        this.setBlockType(position.getX(), position.getY(), position.getZ(), type);
    }

    @Override
    public void setBlockType(int x, int y, int z, BlockType type) {
        this.setBlock(x, y, z, type.getDefaultState());
    }

    @Override
    public void setBlock(int x, int y, int z, BlockState block) {
        this.checkRange(x, y, z);
        this.blocks.set(this.getIndex(x, y, z), LanternBlocks.reg().getInternalStateId(block));
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        this.checkRange(x, y, z);
        short blockState = this.blocks.get(this.getIndex(x, y, z));
        BlockState block = LanternBlocks.reg().getStateByInternalId(blockState);
        return block == null ? this.air : block;
    }

    @Override
    public MutableBlockVolume getBlockView(Vector3i newMin, Vector3i newMax) {
        this.checkRange(newMin.getX(), newMin.getY(), newMin.getZ());
        this.checkRange(newMax.getX(), newMax.getY(), newMax.getZ());
        return new MutableBlockViewDownsize(this, newMin, newMax);
    }

    @Override
    public MutableBlockVolume getBlockView(DiscreteTransform3 transform) {
        return new MutableBlockViewTransform(this, transform);
    }

    @Override
    public MutableBlockVolume getRelativeBlockView() {
        return this.getBlockView(DiscreteTransform3.fromTranslation(this.start.negate()));
    }

    @Override
    public UnmodifiableBlockVolume getUnmodifiableBlockView() {
        return new UnmodifiableBlockVolumeWrapper(this);
    }

    @Override
    public MutableBlockVolume getBlockCopy(StorageType type) {
        switch (type) {
            case STANDARD:
                return new ShortArrayMutableBlockBuffer(this.blocks.getArray(), this.start, this.size);
            case THREAD_SAFE:
                return new AtomicShortArrayMutableBlockBuffer(this.blocks.getArray(), this.start, this.size);
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    @Override
    public ImmutableBlockVolume getImmutableBlockCopy() {
        return new ShortArrayImmutableBlockBuffer(this.blocks.getArray(), this.start, this.size);
    }
}
