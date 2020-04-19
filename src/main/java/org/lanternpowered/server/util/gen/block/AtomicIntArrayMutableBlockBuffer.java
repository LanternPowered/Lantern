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

import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.lanternpowered.server.util.collect.array.concurrent.AtomicIntegerArrayHelper;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.extent.ImmutableBlockVolume;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.math.vector.Vector3i;

import java.util.concurrent.atomic.AtomicIntegerArray;

public class AtomicIntArrayMutableBlockBuffer extends AbstractMutableBlockBuffer implements MutableBlockVolume {

    private final BlockState air = BlockTypes.AIR.getDefaultState();
    private final AtomicIntegerArray blocks;

    public AtomicIntArrayMutableBlockBuffer(Vector3i start, Vector3i size) {
        super(start, size);
        this.blocks = new AtomicIntegerArray(size.getX() * size.getY() * size.getZ());
    }

    public AtomicIntArrayMutableBlockBuffer(int[] blocks, Vector3i start, Vector3i size) {
        super(start, size);
        this.blocks = new AtomicIntegerArray(blocks);
    }

    @Override
    public boolean setBlock(int x, int y, int z, BlockState block) {
        checkRange(x, y, z);
        this.blocks.set(index(x, y, z), (short) BlockRegistryModule.get().getStateInternalId(block));
        return true;
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        checkRange(x, y, z);
        final int blockState = this.blocks.get(index(x, y, z));
        return BlockRegistryModule.get().getStateByInternalId(blockState).orElse(this.air);
    }

    @Override
    public MutableBlockVolume getBlockCopy(StorageType type) {
        final int[] intArray = AtomicIntegerArrayHelper.toArray(this.blocks);
        switch (type) {
            case STANDARD:
                return new IntArrayMutableBlockBuffer(intArray, this.start, this.size);
            case THREAD_SAFE:
                return new AtomicIntArrayMutableBlockBuffer(intArray, this.start, this.size);
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    @Override
    public ImmutableBlockVolume getImmutableBlockCopy() {
        return IntArrayImmutableBlockBuffer.newWithoutArrayClone(AtomicIntegerArrayHelper.toArray(this.blocks), this.start, this.size);
    }
}
