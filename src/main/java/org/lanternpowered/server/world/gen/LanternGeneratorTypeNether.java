package org.lanternpowered.server.world.gen;

import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.gen.WorldGenerator;

public class LanternGeneratorTypeNether extends LanternGeneratorType {

    public LanternGeneratorTypeNether(String name) {
        super(name, 128);
    }

    @Override
    public WorldGenerator createGenerator(World world) {
        SingleBiomeGenerator biomeGenerator = new SingleBiomeGenerator(BiomeTypes.HELL);
        // TODO
        return null;
    }

}
