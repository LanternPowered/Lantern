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
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.world.GeneratorType;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.gen.WorldGenerator;

import java.util.Optional;

public abstract class AbstractFlatGeneratorType extends LanternGeneratorType {

    private final static DataQuery FLAT_SEA_LEVEL = DataQuery.of("sea_level");
    private final static DataQuery FLAT_SEA_LEVEL_VALUE = DataQuery.of("value");
    public final static DataQuery SETTINGS = DataQuery.of("customSettings");

    protected AbstractFlatGeneratorType(CatalogKey key) {
        super(key);
    }

    @Override
    public int getSeaLevel(DataView settings) {
        return settings.getInt(SEA_LEVEL).orElseGet(() -> {
            final FlatGeneratorSettings settings1 = getFlatGeneratorSettings(settings);
            final Optional<Integer> optLevel = settings1.getExtraData().getView(FLAT_SEA_LEVEL)
                    .flatMap(v -> v.getInt(FLAT_SEA_LEVEL_VALUE));
            if (optLevel.isPresent()) {
                return optLevel.get();
            }
            int l = -1;
            int i = -1;
            for (FlatLayer layer : settings1.getLayers()) {
                l += layer.getDepth();
                if (layer.getBlockState().getType() != BlockTypes.AIR) {
                    i = l;
                } else if (i != -1) {
                    break;
                }
            }
            return i == -1 ? 0 : i;
        });
    }
    /**
     * Gets the default {@link FlatGeneratorSettings} of this {@link GeneratorType}.
     *
     * @return The flat generator settings
     */
    protected abstract FlatGeneratorSettings getDefaultSettings();

    /**
     * Gets the {@link FlatGeneratorSettings} for the given settings {@link DataView}.
     *
     * @param settings The settings
     * @return The flat generator settings
     */
    private FlatGeneratorSettings getFlatGeneratorSettings(DataView settings) {
        return settings.getString(SETTINGS).map(FlatGeneratorSettingsParser::fromString).orElseGet(this::getDefaultSettings);
    }

    @Override
    public DataContainer getGeneratorSettings() {
        return super.getGeneratorSettings()
                .set(SETTINGS, FlatGeneratorSettingsParser.toString(getDefaultSettings()));
    }

    @Override
    public WorldGenerator createGenerator(World world) {
        final DataContainer settings = world.getProperties().getGeneratorSettings();
        final FlatGeneratorSettings flatGeneratorSettings = getFlatGeneratorSettings(settings);
        final SingleBiomeGenerator biomeGenerator = new SingleBiomeGenerator(flatGeneratorSettings.getBiomeType());
        final FlatGenerationPopulator populatorGenerator = new FlatGenerationPopulator(
                flatGeneratorSettings, getGeneratorHeight(settings));
        return new LanternWorldGenerator(world, biomeGenerator, populatorGenerator);
    }
}
