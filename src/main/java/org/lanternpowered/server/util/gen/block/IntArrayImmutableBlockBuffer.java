/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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
import org.lanternpowered.server.game.registry.type.block.BlockRegistryModule;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.extent.ImmutableBlockVolume;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.extent.StorageType;

public class IntArrayImmutableBlockBuffer extends AbstractImmutableBlockBuffer {

    private final BlockState air = BlockTypes.AIR.getDefaultState();
    private final int[] blocks;

    public IntArrayImmutableBlockBuffer(int[] blocks, Vector3i start, Vector3i size) {
        super(start, size);
        this.blocks = blocks.clone();
    }

    private IntArrayImmutableBlockBuffer(Vector3i start, Vector3i size, int[] blocks) {
        super(start, size);
        this.blocks = blocks;
    }

    @Override
    public BlockState getBlock(int x, int y, int z) {
        checkRange(x, y, z);
        final int blockState = this.blocks[index(x, y, z)];
        return BlockRegistryModule.get().getStateByInternalId(blockState).orElse(this.air);
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

    /**
     * This method doesn't clone the array passed into it. INTERNAL USE ONLY.
     * Make sure your code doesn't leak the reference if you're using it.
     *
     * @param blocks The blocks to store
     * @param start The start of the volume
     * @param size The size of the volume
     * @return A new buffer using the same array reference
     */
    public static ImmutableBlockVolume newWithoutArrayClone(int[] blocks, Vector3i start, Vector3i size) {
        return new IntArrayImmutableBlockBuffer(start, size, blocks);
    }
}
