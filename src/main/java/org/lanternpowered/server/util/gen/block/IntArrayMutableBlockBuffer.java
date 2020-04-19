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
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.extent.ImmutableBlockVolume;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.StorageType;
import org.spongepowered.math.vector.Vector3i;

public class IntArrayMutableBlockBuffer extends AbstractMutableBlockBuffer {

    private final BlockState air = BlockTypes.AIR.getDefaultState();
    private final int[] blocks;

    public IntArrayMutableBlockBuffer(Vector3i start, Vector3i size) {
        this(new int[size.getX() * size.getY() * size.getZ()], start, size);
    }

    public IntArrayMutableBlockBuffer(int[] blocks, Vector3i start, Vector3i size) {
        super(start, size);
        this.blocks = blocks;
    }

    @Override
    public boolean setBlock(int x, int y, int z, BlockState block) {
        checkRange(x, y, z);
        this.blocks[index(x, y, z)] = (short) BlockRegistryModule.get().getStateInternalId(block);
        return true;
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        checkRange(x, y, z);
        final int blockState = this.blocks[index(x, y, z)];
        final BlockState block = BlockRegistryModule.get().getStateByInternalId(blockState).orElse(null);
        return block == null ? this.air : block;
    }

    @Override
    public MutableBlockVolume getBlockCopy(StorageType type) {
        switch (type) {
            case STANDARD:
                return new IntArrayMutableBlockBuffer(this.blocks.clone(), this.start, this.size);
            case THREAD_SAFE:
                return new AtomicIntArrayMutableBlockBuffer(this.blocks, this.start, this.size);
            default:
                throw new UnsupportedOperationException(type.name());
        }
    }

    @Override
    public ImmutableBlockVolume getImmutableBlockCopy() {
        return new IntArrayImmutableBlockBuffer(this.blocks, this.start, this.size);
    }
}
