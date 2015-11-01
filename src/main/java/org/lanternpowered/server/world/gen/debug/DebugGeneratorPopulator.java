/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.world.gen.debug;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Set;

import org.lanternpowered.server.block.LanternBlockType;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.gen.GeneratorPopulator;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Sets;

public final class DebugGeneratorPopulator implements GeneratorPopulator {

    // The height of the plane where all the blocks are set
    private static final int BLOCKS_PLANE = 70;

    // The barrier plane (the bottom of the world)
    private static final int BARRIER_PLANE = 60;

    // All the block states that should be used
    private final BlockState[] blockStateCache;

    // The x size of the plane
    private final int size;

    public DebugGeneratorPopulator(GameRegistry registry) {
        checkNotNull(registry, "registry");
        Set<BlockState> blockStates = Sets.newLinkedHashSet();
        for (BlockType blockType : registry.getAllOf(BlockType.class)) {
            blockStates.addAll(((LanternBlockType) blockType).getAllStates());
        }
        this.blockStateCache = blockStates.toArray(new BlockState[0]);
        this.size = (int) Math.ceil(Math.sqrt((double) this.blockStateCache.length));
    }

    public DebugGeneratorPopulator(Iterable<BlockState> blockStates) {
        this.blockStateCache = Sets.newLinkedHashSet(checkNotNull(blockStates, "blockStates"))
                .toArray(new BlockState[0]);
        this.size = (int) Math.ceil(Math.sqrt((double) this.blockStateCache.length));
    }

    @Override
    public void populate(World world, MutableBlockVolume buffer, ImmutableBiomeArea biomes) {
        Vector3i min = buffer.getBlockMin();
        Vector3i max = buffer.getBlockMax();

        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getZ(); z <= max.getZ(); z++) {
                if (min.getY() <= BARRIER_PLANE && max.getY() >= BARRIER_PLANE) {
                    buffer.setBlock(x, BARRIER_PLANE, z, BlockTypes.BARRIER.getDefaultState());
                }
                if (min.getY() <= BLOCKS_PLANE && max.getY() >= BLOCKS_PLANE &&
                        (x & 0x1) != 0 && (z & 0x1) != 0) {
                    int i = x >> 1;
                    int j = z >> 1;

                    int index = Math.abs(i * this.size + j);
                    if (index < this.blockStateCache.length) {
                        buffer.setBlock(x, BLOCKS_PLANE, z, this.blockStateCache[index]);
                    }
                }
            }
        }
    }
}
