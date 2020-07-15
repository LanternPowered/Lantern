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

import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.world.biome.BiomeTypes;

import java.util.ArrayList;
import java.util.List;

public final class FlatNetherGeneratorType extends AbstractFlatGeneratorType {

    public FlatNetherGeneratorType(ResourceKey key) {
        super(key);
        setDefaultGeneratorHeight(128);
        setDefaultMinimalSpawnHeight(4);
    }

    @Override
    protected FlatGeneratorSettings getDefaultSettings() {
        final List<FlatLayer> layers = new ArrayList<>(5);
        layers.add(new FlatLayer(BlockTypes.BEDROCK, 1));
        layers.add(new FlatLayer(BlockTypes.NETHERRACK, 3));
        layers.add(new FlatLayer(BlockTypes.AIR, 120));
        layers.add(new FlatLayer(BlockTypes.NETHERRACK, 3));
        layers.add(new FlatLayer(BlockTypes.BEDROCK, 1));
        return new FlatGeneratorSettings(BiomeTypes.HELL, layers);
    }
}
