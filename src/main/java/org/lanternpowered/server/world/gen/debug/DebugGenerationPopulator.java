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
package org.lanternpowered.server.world.gen.debug;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Sets;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.ImmutableBiomeVolume;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.gen.GenerationPopulator;
import org.spongepowered.math.vector.Vector3i;

import java.util.LinkedHashSet;
import java.util.Set;

public final class DebugGenerationPopulator implements GenerationPopulator {

    // The height of the plane where all the blocks are set
    private static final int BLOCKS_PLANE = 70;

    // The barrier plane (the bottom of the world)
    private static final int BARRIER_PLANE = 60;

    // All the block states that should be used
    private final BlockState[] blockStateCache;

    // The x/z size of the plane
    private final int size;

    public DebugGenerationPopulator(GameRegistry registry) {
        checkNotNull(registry, "registry");
        final Set<BlockState> blockStates = Sets.newLinkedHashSet();
        for (BlockType blockType : registry.getAllOf(BlockType.class)) {
            blockStates.addAll(blockType.getAllBlockStates());
        }
        this.blockStateCache = blockStates.toArray(new BlockState[blockStates.size()]);
        this.size = (int) Math.ceil(Math.sqrt((double) this.blockStateCache.length));
    }

    public DebugGenerationPopulator(Iterable<BlockState> blockStates) {
        final LinkedHashSet<BlockState> states = Sets.newLinkedHashSet(checkNotNull(blockStates, "blockStates"));
        this.blockStateCache = states.toArray(new BlockState[states.size()]);
        this.size = (int) Math.ceil(Math.sqrt((double) this.blockStateCache.length));
    }

    @Override
    public void populate(World world, MutableBlockVolume buffer, ImmutableBiomeVolume biomes) {
        final Vector3i min = buffer.getBlockMin();
        final Vector3i max = buffer.getBlockMax();

        final boolean placeBarriers = min.getY() <= BARRIER_PLANE && max.getY() >= BARRIER_PLANE;
        final boolean placeBlocks = min.getY() <= BLOCKS_PLANE && max.getY() >= BLOCKS_PLANE;

        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getZ(); z <= max.getZ(); z++) {
                if (placeBarriers) {
                    buffer.setBlock(x, BARRIER_PLANE, z, BlockTypes.BARRIER.getDefaultState());
                }
                if (placeBlocks && x > 0 && z > 0 && x % 2 != 0 && z % 2 != 0) {
                    int index = (x / 2) * this.size + (z / 2);
                    if (index >= 0 && index < this.blockStateCache.length) {
                        buffer.setBlock(x, BLOCKS_PLANE, z, this.blockStateCache[index]);
                    }
                }
            }
        }
    }
}
