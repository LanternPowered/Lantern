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
package org.lanternpowered.server.world.biome;

import java.util.List;

import org.lanternpowered.server.catalog.LanternPluginCatalogType;
import org.lanternpowered.server.util.Lists2;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.GroundCoverLayer;
import org.spongepowered.api.world.gen.GeneratorPopulator;
import org.spongepowered.api.world.gen.Populator;

import com.google.common.collect.Lists;

public class LanternBiomeType extends LanternPluginCatalogType implements BiomeType {

    // Using concurrent lists, we have no idea what plugin devs will do with them...
    private final List<GroundCoverLayer> groundCoverLayers = Lists2.nonNullOf(Lists.newCopyOnWriteArrayList());
    private final List<GeneratorPopulator> generatorPopulators = Lists2.nonNullOf(Lists.newCopyOnWriteArrayList());
    private final List<Populator> populators = Lists2.nonNullOf(Lists.newCopyOnWriteArrayList());

    private double temperature;
    private double humidity;

    private float minHeight;
    private float maxHeight;

    public LanternBiomeType(String pluginId, String identifier) {
        super(pluginId, identifier);
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
