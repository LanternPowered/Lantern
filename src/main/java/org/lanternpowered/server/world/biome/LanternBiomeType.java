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

import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeGenerationSettings;

public class LanternBiomeType extends AbstractBiomeType {

    private final LanternBiomeGenerationSettings defaultSettings = new LanternBiomeGenerationSettings();

    public LanternBiomeType(CatalogKey key) {
        super(key);
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
}
