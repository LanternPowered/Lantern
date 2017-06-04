/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
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

import org.lanternpowered.server.world.gen.LanternGeneratorType;
import org.lanternpowered.server.world.gen.LanternWorldGenerator;
import org.lanternpowered.server.world.gen.SingleBiomeGenerator;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.gen.WorldGenerator;

public abstract class AbstractFlatGeneratorType extends LanternGeneratorType {

    public final static DataQuery SETTINGS = DataQuery.of("customSettings");

    public AbstractFlatGeneratorType(String pluginId, String name, int minimalSpawnHeight) {
        super(pluginId, name, minimalSpawnHeight);
    }

    public AbstractFlatGeneratorType(String pluginId, String name, int generatorHeight, int minimalSpawnHeight) {
        super(pluginId, name, generatorHeight, minimalSpawnHeight);
    }

    protected AbstractFlatGeneratorType(String pluginId, String name) {
        super(pluginId, name);
    }

    protected abstract FlatGeneratorSettings getDefaultSettings();

    @Override
    public DataContainer getGeneratorSettings() {
        return super.getGeneratorSettings().set(SETTINGS, FlatGeneratorSettingsParser.toString(
                getDefaultSettings()));
    }

    @Override
    public WorldGenerator createGenerator(World world) {
        final DataContainer generatorSettings = world.getProperties().getGeneratorSettings();
        FlatGeneratorSettings settings = null;
        if (generatorSettings.contains(SETTINGS)) {
            settings = FlatGeneratorSettingsParser.fromString(generatorSettings.getString(SETTINGS).get());
        }
        if (settings == null) {
            settings = getDefaultSettings();
        }
        final SingleBiomeGenerator biomeGenerator = new SingleBiomeGenerator(settings.getBiomeType());
        final FlatGenerationPopulator populatorGenerator = new FlatGenerationPopulator(settings,
                (LanternGeneratorType) world.getProperties().getGeneratorType());
        return new LanternWorldGenerator(world, biomeGenerator, populatorGenerator);
    }

}
