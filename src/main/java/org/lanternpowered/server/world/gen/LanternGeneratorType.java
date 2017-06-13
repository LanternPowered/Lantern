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
package org.lanternpowered.server.world.gen;

import org.lanternpowered.server.catalog.PluginCatalogType;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;

public abstract class LanternGeneratorType extends PluginCatalogType.Base implements IGeneratorType {

    // The maximum height the generator will generate the world,
    // for example 128 blocks in the nether and in overworld 256
    private int generatorHeight;

    // The minimal spawn height
    private int minimalSpawnHeight;

    private int seaLevel;

    protected LanternGeneratorType(String pluginId, String name) {
        super(pluginId, name);
        setDefaultGeneratorHeight(256);
        setDefaultMinimalSpawnHeight(1);
        setDefaultSeaLevel(62);
    }

    protected void setDefaultGeneratorHeight(int generatorHeight) {
        this.generatorHeight = generatorHeight;
    }

    protected void setDefaultMinimalSpawnHeight(int minimalSpawnHeight) {
        this.minimalSpawnHeight = minimalSpawnHeight;
    }

    protected void setDefaultSeaLevel(int seaLevel) {
        this.seaLevel = seaLevel;
    }

    @Override
    public DataContainer getGeneratorSettings() {
        return DataContainer.createNew();
    }

    @Override
    public int getSeaLevel(DataView settings) {
        return settings.getInt(SEA_LEVEL).orElse(this.seaLevel);
    }

    @Override
    public int getMinimalSpawnHeight(DataView settings) {
        return settings.getInt(MINIMAL_SPAWN_HEIGHT).orElse(this.minimalSpawnHeight);
    }

    @Override
    public int getGeneratorHeight(DataView settings) {
        return settings.getInt(GENERATOR_HEIGHT).orElse(this.generatorHeight);
    }
}
