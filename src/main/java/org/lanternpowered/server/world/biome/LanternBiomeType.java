package org.lanternpowered.server.world.biome;

import java.util.List;

import org.lanternpowered.server.catalog.LanternCatalogType;
import org.lanternpowered.server.util.NonNullArrayList;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.GroundCoverLayer;
import org.spongepowered.api.world.gen.GeneratorPopulator;
import org.spongepowered.api.world.gen.Populator;

public class LanternBiomeType extends LanternCatalogType implements BiomeType {

    private final List<GroundCoverLayer> groundCoverLayers = new NonNullArrayList<GroundCoverLayer>();
    private final List<GeneratorPopulator> generatorPopulators = new NonNullArrayList<GeneratorPopulator>();
    private final List<Populator> populators = new NonNullArrayList<Populator>();

    private double temperature;
    private double humidity;

    private float minHeight;
    private float maxHeight;

    public LanternBiomeType(String identifier, String name) {
        super(identifier, name);
    }

    @Override
    public double getTemperature() {
        return this.temperature;
    }

    @Override
    public double getHumidity() {
        return this.humidity;
    }

    @Override
    public float getMinHeight() {
        return this.minHeight;
    }

    @Override
    public float getMaxHeight() {
        return this.maxHeight;
    }

    @Override
    public List<GroundCoverLayer> getGroundCover() {
        return this.groundCoverLayers;
    }

    @Override
    public List<GeneratorPopulator> getGeneratorPopulators() {
        return this.generatorPopulators;
    }

    @Override
    public List<Populator> getPopulators() {
        return this.populators;
    }
}
