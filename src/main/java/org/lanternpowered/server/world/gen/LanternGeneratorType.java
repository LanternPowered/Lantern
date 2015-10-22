/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
 * Copyright (c) Contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and or sell
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

import org.lanternpowered.server.catalog.LanternPluginCatalogType;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.world.GeneratorType;

public abstract class LanternGeneratorType extends LanternPluginCatalogType implements GeneratorType {

    // The maximum height the generator will generate the world,
    // for example 128 blocks in the nether and in overworld 256
    private final int generatorHeight;

    // The minimal spawn height
    private final int minimalSpawnHeight;

    public LanternGeneratorType(String pluginId, String name) {
        this(pluginId, name, 256, 1);
    }

    public LanternGeneratorType(String pluginId, String name, int minimalSpawnHeight) {
        this(pluginId, name, 256, minimalSpawnHeight);
    }

    public LanternGeneratorType(String pluginId, String name, int generatorHeight,
            int minimalSpawnHeight) {
        super(pluginId, name);
        this.minimalSpawnHeight = minimalSpawnHeight;
        this.generatorHeight = generatorHeight;
    }

    @Override
    public DataContainer getGeneratorSettings() {
        return new MemoryDataContainer();
    }

    public int getMinimalSpawnHeight() {
        return this.minimalSpawnHeight;
    }

    public int getGeneratorHeight() {
        return this.generatorHeight;
    }
}
