package org.lanternpowered.server.world.gen.debug;

import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.world.gen.LanternGeneratorType;
import org.lanternpowered.server.world.gen.LanternWorldGenerator;
import org.lanternpowered.server.world.gen.SingleBiomeGenerator;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.gen.WorldGenerator;

public final class DebugGeneratorType extends LanternGeneratorType {

    public DebugGeneratorType(String name) {
        super(name);
    }

    @Override
    public WorldGenerator createGenerator(World world) {
        return new LanternWorldGenerator(new DebugGeneratorPopulator(LanternGame.get().getRegistry()),
                new SingleBiomeGenerator(BiomeTypes.PLAINS));
    }
}
