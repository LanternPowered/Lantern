/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.world.gen.flat;

import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.biome.BiomeTypes;

import java.util.ArrayList;
import java.util.List;

public final class FlatOverworldGeneratorType extends AbstractFlatGeneratorType {

    public FlatOverworldGeneratorType(CatalogKey key) {
        super(key);
    }

    @Override
    public FlatGeneratorSettings getDefaultSettings() {
        final List<FlatLayer> layers = new ArrayList<>(3);
        layers.add(new FlatLayer(BlockTypes.BEDROCK, 1));
        layers.add(new FlatLayer(BlockTypes.DIRT, 2));
        layers.add(new FlatLayer(BlockTypes.GRASS_BLOCK, 1));
        return new FlatGeneratorSettings(BiomeTypes.PLAINS, layers);
    }
}
