/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
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
package org.lanternpowered.server.util.gen.block;

import com.flowpowered.math.vector.Vector3i;
import org.lanternpowered.server.game.registry.Registries;
import org.lanternpowered.server.util.concurrent.AtomicShortArray;
import org.lanternpowered.server.world.extent.worker.LanternMutableBlockVolumeWorker;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.extent.ImmutableBlockVolume;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.api.world.extent.worker.MutableBlockVolumeWorker;

@NonnullByDefault
public class AtomicShortArrayMutableBlockBuffer extends AbstractMutableBlockBuffer implements MutableBlockVolume {

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
    public void setBlock(int x, int y, int z, BlockState block) {
        this.checkRange(x, y, z);
        this.blocks.set(this.index(x, y, z), Registries.getBlockRegistry().getStateInternalIdAndData(block));
    }

    @Override
    public MutableBlockVolumeWorker<? extends MutableBlockVolume> getBlockWorker() {
        return new LanternMutableBlockVolumeWorker<>(this);
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        this.checkRange(x, y, z);
        final short blockState = this.blocks.get(this.index(x, y, z));
        final BlockState block = Registries.getBlockRegistry().getStateByInternalIdAndData(blockState).orElse(BlockTypes.AIR.getDefaultState());
        return block == null ? this.air : block;
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
        return ShortArrayImmutableBlockBuffer.newWithoutArrayClone(this.blocks.getArray(), this.start, this.size);
    }
}
