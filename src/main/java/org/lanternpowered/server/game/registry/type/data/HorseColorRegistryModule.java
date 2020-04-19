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
package org.lanternpowered.server.game.registry.type.data;

import org.lanternpowered.server.data.type.LanternHorseColor;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.type.HorseColor;
import org.spongepowered.api.data.type.HorseColors;

public class HorseColorRegistryModule extends DefaultCatalogRegistryModule<HorseColor> {

    public HorseColorRegistryModule() {
        super(HorseColors.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternHorseColor(CatalogKey.minecraft("white"), 0));
        register(new LanternHorseColor(CatalogKey.minecraft("creamy"), 1));
        register(new LanternHorseColor(CatalogKey.minecraft("chestnut"), 2));
        register(new LanternHorseColor(CatalogKey.minecraft("brown"), 3));
        register(new LanternHorseColor(CatalogKey.minecraft("black"), 4));
        register(new LanternHorseColor(CatalogKey.minecraft("gray"), 5));
        register(new LanternHorseColor(CatalogKey.minecraft("dark_brown"), 6));
    }
}
