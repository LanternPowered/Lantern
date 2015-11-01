/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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

public final class FlatGeneratorType extends LanternGeneratorType {

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

    public FlatGeneratorType(String pluginId, String name) {
        super(pluginId, name);
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
