package org.lanternpowered.server.world.gen.flat;

import java.util.List;

import org.lanternpowered.server.world.gen.LanternGeneratorType;
import org.lanternpowered.server.world.gen.LanternWorldGenerator;
import org.lanternpowered.server.world.gen.SingleBiomeGenerator;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.gen.WorldGenerator;

import com.google.common.collect.Lists;

public class FlatGeneratorType extends LanternGeneratorType {

    private final static DataQuery STRING_VALUE = DataQuery.of("customSettings");

    /**
     * Creates the default settings of the flat generator.
     * 
     * @return the default settings
     */
    public static FlatGeneratorSettings getDefaultSettings() {
        List<FlatLayer> layers = Lists.newArrayListWithCapacity(3);
        layers.add(new FlatLayer(BlockTypes.BEDROCK, 1));
        layers.add(new FlatLayer(BlockTypes.DIRT, 2));
        layers.add(new FlatLayer(BlockTypes.GRASS, 1));
        return new FlatGeneratorSettings(BiomeTypes.PLAINS, layers);
    }

    public FlatGeneratorType(String name) {
        super(name);
    }

    @Override
    public DataContainer getGeneratorSettings() {
        return super.getGeneratorSettings().set(STRING_VALUE, FlatGeneratorSettingsParser.toString(
                getDefaultSettings()));
    }

    @Override
    public WorldGenerator createGenerator(World world) {
        DataContainer generatorSettings = world.getProperties().getGeneratorSettings();
        FlatGeneratorSettings settings = null;
        if (generatorSettings.contains(STRING_VALUE)) {
            settings = FlatGeneratorSettingsParser.fromString(generatorSettings.getString(STRING_VALUE).get());
        }
        if (settings == null) {
            settings = getDefaultSettings();
        }
        SingleBiomeGenerator biomeGenerator = new SingleBiomeGenerator(settings.getBiomeType());
        FlatGeneratorPopulator populatorGenerator = new FlatGeneratorPopulator(settings,
                (LanternGeneratorType) world.getProperties().getGeneratorType());
        return new LanternWorldGenerator(populatorGenerator, biomeGenerator);
    }
}
