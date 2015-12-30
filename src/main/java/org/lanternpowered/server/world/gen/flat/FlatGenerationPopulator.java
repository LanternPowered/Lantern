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
package org.lanternpowered.server.world.gen.flat;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.lanternpowered.server.world.gen.LanternGeneratorType;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.util.annotation.NonnullByDefault;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.gen.GenerationPopulator;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;

@NonnullByDefault
public final class FlatGenerationPopulator implements GenerationPopulator {

    // Using a cache to increase generation performance
    private final BlockState[] blockStateCache;

    public FlatGenerationPopulator(FlatGeneratorSettings settings, LanternGeneratorType generatorType) {
        this(settings, checkNotNull(generatorType, "generatorType").getGeneratorHeight());
    }

    public FlatGenerationPopulator(FlatGeneratorSettings settings, int generatorHeight) {
        checkNotNull(settings, "settings");

        List<BlockState> blockStates = Lists.newArrayList();
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
    public void populate(World world, MutableBlockVolume buffer, ImmutableBiomeArea biomes) {
        final Vector3i min = buffer.getBlockMin();
        final Vector3i max = buffer.getBlockMax();

        final int height = this.blockStateCache.length;
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getZ(); z <= max.getZ(); z++) {
                for (int y = min.getY(); y <= max.getY(); y++) {
                    if (y >= height) {
                        break;
                    }
                    buffer.setBlock(x, y, z, this.blockStateCache[y]);
                }
            }
        }
    }

}
