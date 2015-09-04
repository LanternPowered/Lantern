package org.lanternpowered.server.world.gen;

import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.world.GeneratorType;

public abstract class LanternGeneratorType implements GeneratorType {

    private final String name;

    // The maximum height the generator will generate the world,
    // for example 128 blocks in the nether and in overworld 256
    private final int generatorHeight;

    public LanternGeneratorType(String name) {
        this(name, 256);
    }

    public LanternGeneratorType(String name, int generatorHeight) {
        this.generatorHeight = generatorHeight;
        this.name = name;
    }

    @Override
    public String getId() {
        return this.name;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public DataContainer getGeneratorSettings() {
        // By default empty, only for special generators
        return new MemoryDataContainer();
    }

    public int getGeneratorHeight() {
        return this.generatorHeight;
    }
}
