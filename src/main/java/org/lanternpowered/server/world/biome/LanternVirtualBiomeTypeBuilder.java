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
package org.lanternpowered.server.world.biome;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static org.lanternpowered.server.util.Conditions.checkNotNullOrEmpty;

import org.lanternpowered.api.ResourceKeys;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeGenerationSettings;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.BiomeTypes;
import org.spongepowered.api.world.biome.VirtualBiomeType;
import org.spongepowered.api.world.biome.VirtualBiomeType.Builder;

import java.util.function.Function;

public class LanternVirtualBiomeTypeBuilder implements VirtualBiomeType.Builder {

    private String name;
    private double temperature;
    private double humidity;
    private BiomeType persisted;
    private Function<World, BiomeGenerationSettings> settingsBuilder;

    public LanternVirtualBiomeTypeBuilder() {
        reset();
    }

    @Override
    public Builder name(String name) {
        this.name = checkNotNull(name, "name");
        return this;
    }

    @Override
    public Builder temperature(double temperature) {
        this.temperature = temperature;
        return this;
    }

    @Override
    public Builder humidity(double humidity) {
        this.humidity = humidity;
        return this;
    }

    @Override
    public Builder persistedType(BiomeType biome) {
        checkNotNull(biome, "biome");
        checkArgument(!(biome instanceof VirtualBiomeType), "Persisted type cannot be a virtual biome.");
        this.persisted = biome;
        return this;
    }

    @Override
    public Builder from(VirtualBiomeType value) {
        this.name = value.getName();
        this.temperature = value.getTemperature();
        this.humidity = value.getHumidity();
        return this;
    }

    @Override
    public Builder settingsBuilder(Function<World, BiomeGenerationSettings> settingsBuilder) {
        this.settingsBuilder = checkNotNull(settingsBuilder, "settingsBuilder");
        return this;
    }

    @Override
    public Builder reset() {
        this.name = null;
        this.temperature = 0;
        this.humidity = 0;
        this.persisted = BiomeTypes.VOID;
        this.settingsBuilder = null;
        return this;
    }

    @Override
    public VirtualBiomeType build(String id) throws IllegalStateException {
        checkNotNull(this.name, "name");
        checkNotNullOrEmpty(id, "id");
        checkNotNull(this.persisted, "persistedBiome");
        checkNotNull(this.settingsBuilder, "settingsBuilder");
        final int index = id.indexOf(':');
        final String pluginId;
        if (index != -1) {
            pluginId = id.substring(0, index);
            id = id.substring(index + 1);
        } else {
            pluginId = "minecraft";
        }
        final LanternVirtualBiomeType biomeType = new LanternVirtualBiomeType(
                ResourceKeys.of(pluginId, id), this.settingsBuilder, this.persisted);
        biomeType.setHumidity(this.humidity);
        biomeType.setTemperature(this.temperature);
        return biomeType;
    }
}
