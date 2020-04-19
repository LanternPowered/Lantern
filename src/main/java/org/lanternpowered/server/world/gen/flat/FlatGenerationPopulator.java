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
package org.lanternpowered.server.world.gen.flat;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.ImmutableBiomeVolume;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.gen.GenerationPopulator;
import org.spongepowered.math.vector.Vector3i;

import java.util.ArrayList;
import java.util.List;

public final class FlatGenerationPopulator implements GenerationPopulator {

    // Using a cache to increase generation performance
    private final BlockState[] blockStateCache;

    public FlatGenerationPopulator(FlatGeneratorSettings settings, int generatorHeight) {
        checkNotNull(settings, "settings");

        List<BlockState> blockStates = new ArrayList<>();
        for (FlatLayer layer : settings.getLayers()) {
            final BlockState blockState = layer.getBlockState();
            for (int i = 0; i < layer.getDepth(); i++) {
                blockStates.add(blockState);
            }
        }

        if (blockStates.size() > generatorHeight) {
            blockStates = blockStates.subList(0, generatorHeight);
        }

        this.blockStateCache = blockStates.toArray(new BlockState[blockStates.size()]);
    }

    @Override
    public void populate(World world, MutableBlockVolume buffer, ImmutableBiomeVolume biomes) {
        final Vector3i min = buffer.getBlockMin();
        final Vector3i max = buffer.getBlockMax();

        final int height = this.blockStateCache.length;
        for (int y = min.getY(); y < height; y++) {
            if (this.blockStateCache[y] == BlockTypes.AIR) {
                continue;
            }
            for (int x = min.getX(); x <= max.getX(); x++) {
                for (int z = min.getZ(); z <= max.getZ(); z++) {
                    buffer.setBlock(x, y, z, this.blockStateCache[y]);
                }
            }
        }
    }
}
