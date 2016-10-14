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
package org.lanternpowered.server.world.biome;

import org.lanternpowered.server.catalog.PluginCatalogType;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeGenerationSettings;
import org.spongepowered.api.world.biome.BiomeType;

public class LanternBiomeType extends PluginCatalogType.Base implements BiomeType {

    private final LanternBiomeGenerationSettings defaultSettings = new LanternBiomeGenerationSettings();

    private double temperature;
    private double humidity;

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
    public BiomeGenerationSettings createDefaultGenerationSettings(World world) {
        return this.defaultSettings.copy();
    }

    /**
     * Gets the default biome generation settings.
     * 
     * @return The default biome generation settings
     */
    public LanternBiomeGenerationSettings getDefaultGenerationSettings() {
        return this.defaultSettings;
    }

    public void setTemperature(double temperature) {
        this.temperature = temperature;
    }

    public void setHumidity(double humidity) {
        this.humidity = humidity;
    }

}
