package org.lanternpowered.server.world.gen.flat;

import java.util.List;

import org.lanternpowered.server.world.gen.LanternGeneratorType;
import org.spongepowered.api.block.BlockState;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.extent.ImmutableBiomeArea;
import org.spongepowered.api.world.extent.MutableBlockVolume;
import org.spongepowered.api.world.gen.GeneratorPopulator;

import com.flowpowered.math.vector.Vector3i;
import com.google.common.collect.Lists;

public final class FlatGeneratorPopulator implements GeneratorPopulator {

    // Using a cache to increase generation performance
    private final BlockState[] blockStateCache;

    public FlatGeneratorPopulator(FlatGeneratorSettings settings, LanternGeneratorType generatorType) {
        List<BlockState> blockStates = Lists.newArrayList();
        List<FlatLayer> layers = settings.getLayers();

        for (FlatLayer layer : layers) {
            BlockState blockState = layer.getBlockState();
            for (int i = 0; i < layer.getDepth(); i++) {
                blockStates.add(blockState);
            }
        }

        if (blockStates.size() > generatorType.getGeneratorHeight()) {
            blockStates = blockStates.subList(0, generatorType.getGeneratorHeight());
        }

        this.blockStateCache = blockStates.toArray(new BlockState[] {});
    }

    @Override
    public void populate(World world, MutableBlockVolume buffer, ImmutableBiomeArea biomes) {
        Vector3i min = buffer.getBlockMin();
        Vector3i max = buffer.getBlockMax();

        int height = this.blockStateCache.length;
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
