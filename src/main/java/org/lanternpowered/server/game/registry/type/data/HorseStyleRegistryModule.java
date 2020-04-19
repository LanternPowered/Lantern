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

import org.lanternpowered.server.data.type.LanternHorseStyle;
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.data.type.HorseStyle;
import org.spongepowered.api.data.type.HorseStyles;

public class HorseStyleRegistryModule extends DefaultCatalogRegistryModule<HorseStyle> {

    public HorseStyleRegistryModule() {
        super(HorseStyles.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternHorseStyle(CatalogKey.minecraft("none"), 0));
        register(new LanternHorseStyle(CatalogKey.minecraft("white"), 1));
        register(new LanternHorseStyle(CatalogKey.minecraft("whitefield"), 2));
        register(new LanternHorseStyle(CatalogKey.minecraft("white_dots"), 3));
        register(new LanternHorseStyle(CatalogKey.minecraft("black_dots"), 4));
    }
}
