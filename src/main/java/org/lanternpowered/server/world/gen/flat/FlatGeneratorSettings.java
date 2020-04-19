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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.data.persistence.DataContainer;
import org.spongepowered.api.world.biome.BiomeType;

import java.util.List;

public final class FlatGeneratorSettings {

    private final List<FlatLayer> layers;
    private final BiomeType biomeType;
    private final DataContainer extraData;

    public FlatGeneratorSettings(BiomeType biomeType, List<FlatLayer> layers) {
        this(biomeType, layers, DataContainer.createNew());
    }

    public FlatGeneratorSettings(BiomeType biomeType, List<FlatLayer> layers, DataContainer extraData) {
        this.layers = ImmutableList.copyOf(checkNotNull(layers, "layers"));
        this.extraData = checkNotNull(extraData, "extraData");
        this.biomeType = checkNotNull(biomeType, "biomeType");
    }

    public BiomeType getBiomeType() {
        return this.biomeType;
    }

    public List<FlatLayer> getLayers() {
        return this.layers;
    }

    public DataContainer getExtraData() {
        return this.extraData;
    }

    @Override
    public String toString() {
        return FlatGeneratorSettingsParser.toString(this);
    }
}
