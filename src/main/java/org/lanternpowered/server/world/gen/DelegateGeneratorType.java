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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import org.lanternpowered.server.catalog.PluginCatalogType;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.gen.WorldGenerator;

public final class DelegateGeneratorType extends PluginCatalogType.Base implements IGeneratorType {

    private GeneratorType generatorType;

    private int minimalSpawnHeight;
    private int generatorHeight;
    private int seaLevel;

    public DelegateGeneratorType(String pluginId, String id, GeneratorType generatorType) {
        super(pluginId, id);
        setGeneratorType(generatorType);
    }

    @Override
    public String getName() {
        return this.generatorType.getName();
    }

    /**
     * Gets the actual {@link GeneratorType}.
     *
     * @return The generator type
     */
    public GeneratorType getGeneratorType() {
        return this.generatorType;
    }

    /**
     * Sets the actual {@link GeneratorType}.
     *
     * @param generatorType The generator type
     */
    public void setGeneratorType(GeneratorType generatorType) {
        checkNotNull(generatorType, "generatorType");
        this.generatorType = generatorType;
        this.minimalSpawnHeight = Integer.MAX_VALUE;
        this.generatorHeight = Integer.MAX_VALUE;
        this.seaLevel = Integer.MAX_VALUE;
    }

    @Override
    public int getMinimalSpawnHeight(DataView settings) {
        if (this.minimalSpawnHeight == Integer.MAX_VALUE) {
            this.minimalSpawnHeight = IGeneratorType.getMinimalSpawnHeight(this.generatorType, settings);
        }
        return this.minimalSpawnHeight;
    }

    @Override
    public int getGeneratorHeight(DataView settings) {
        if (this.generatorHeight == Integer.MAX_VALUE) {
            this.generatorHeight = IGeneratorType.getGeneratorHeight(this.generatorType, settings);
        }
        return this.generatorHeight;
    }

    @Override
    public int getSeaLevel(DataView settings) {
        if (this.seaLevel == Integer.MAX_VALUE) {
            this.seaLevel = IGeneratorType.getSeaLevel(this.generatorType, settings);
        }
        return this.seaLevel;
    }

    @Override
    public DataContainer getGeneratorSettings() {
        return this.generatorType.getGeneratorSettings();
    }

    @Override
    public WorldGenerator createGenerator(World world) {
        return this.generatorType.createGenerator(world);
    }

    @Override
    public MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("backing", this.generatorType.getId());
    }
}
