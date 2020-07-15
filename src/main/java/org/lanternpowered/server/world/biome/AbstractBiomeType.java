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

import org.lanternpowered.server.catalog.DefaultCatalogType;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.world.biome.BiomeType;

public abstract class AbstractBiomeType extends DefaultCatalogType implements BiomeType {

    private double temperature;
    private double humidity;

    public AbstractBiomeType(ResourceKey key) {
        super(key);
    }

    @Override
    public double getTemperature() {
        return this.temperature;
    }

    @Override
    public double getHumidity() {
        return this.humidity;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }
}
