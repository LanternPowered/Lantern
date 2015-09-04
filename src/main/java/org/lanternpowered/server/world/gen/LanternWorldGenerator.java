package org.lanternpowered.server.world.gen;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import org.lanternpowered.server.util.NonNullArrayList;
import org.spongepowered.api.world.gen.BiomeGenerator;
import org.spongepowered.api.world.gen.GeneratorPopulator;
import org.spongepowered.api.world.gen.Populator;
import org.spongepowered.api.world.gen.WorldGenerator;

public class LanternWorldGenerator implements WorldGenerator {

    private final List<GeneratorPopulator> generatorPopulators = new NonNullArrayList<>();
    private final List<Populator> populators = new NonNullArrayList<>();

    private GeneratorPopulator baseGeneratorPopulator;
    private BiomeGenerator biomeGenerator;

    public LanternWorldGenerator(GeneratorPopulator baseGeneratorPopulator,
            BiomeGenerator biomeGenerator) {
        this.baseGeneratorPopulator = checkNotNull(baseGeneratorPopulator, "baseGeneratorPopulator");
        this.biomeGenerator = checkNotNull(biomeGenerator, "biomeGenerator");
    }

    @Override
    public GeneratorPopulator getBaseGeneratorPopulator() {
        return this.baseGeneratorPopulator;
    }

    @Override
    public void setBaseGeneratorPopulator(GeneratorPopulator generator) {
        this.baseGeneratorPopulator = checkNotNull(generator, "generator");
    }

    @Override
    public List<GeneratorPopulator> getGeneratorPopulators() {
        return this.generatorPopulators;
    }

    @Override
    public List<Populator> getPopulators() {
        return this.populators;
    }

    @Override
    public BiomeGenerator getBiomeGenerator() {
        return this.biomeGenerator;
    }

    @Override
    public void setBiomeGenerator(BiomeGenerator biomeGenerator) {
        this.biomeGenerator = checkNotNull(biomeGenerator, "biomeGenerator");
    }
}
