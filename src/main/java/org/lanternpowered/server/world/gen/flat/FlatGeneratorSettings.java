package org.lanternpowered.server.world.gen.flat;

import java.util.List;

import org.spongepowered.api.world.biome.BiomeType;

import com.google.common.collect.ImmutableList;

public final class FlatGeneratorSettings {

    private final List<FlatLayer> layers;
    private final BiomeType biomeType;

    public FlatGeneratorSettings(BiomeType biomeType, List<FlatLayer> layers) {
        this.layers = ImmutableList.copyOf(layers);
        this.biomeType = biomeType;
    }

    public BiomeType getBiomeType() {
        return this.biomeType;
    }

    public List<FlatLayer> getLayers() {
        return this.layers;
    }
}
