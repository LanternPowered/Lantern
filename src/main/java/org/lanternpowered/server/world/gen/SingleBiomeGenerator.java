package org.lanternpowered.server.world.gen;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.extent.MutableBiomeArea;
import org.spongepowered.api.world.gen.BiomeGenerator;

import com.flowpowered.math.vector.Vector2i;

public class SingleBiomeGenerator implements BiomeGenerator {

    private final BiomeType biomeType;

    public SingleBiomeGenerator(BiomeType biomeType) {
        this.biomeType = checkNotNull(biomeType, "biomeType");
    }

    @Override
    public void generateBiomes(MutableBiomeArea buffer) {
        Vector2i min = buffer.getBiomeMin();
        Vector2i max = buffer.getBiomeMax();
        for (int x = min.getX(); x <= max.getX(); x++) {
            for (int z = min.getY(); z <= max.getY(); z++) {
                buffer.setBiome(x, z, this.biomeType);
            }
        }
    }
}
