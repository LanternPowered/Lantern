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

import org.lanternpowered.server.data.type.LanternDyeColor;
import org.lanternpowered.server.game.registry.InternalPluginCatalogRegistryModule;
import org.spongepowered.api.data.type.DyeColor;
import org.spongepowered.api.data.type.DyeColors;

import java.util.Arrays;

public class DyeColorRegistryModule extends InternalPluginCatalogRegistryModule<DyeColor> {

    private static final DyeColorRegistryModule INSTANCE = new DyeColorRegistryModule();

    public static DyeColorRegistryModule get() {
        return INSTANCE;
    }

    private DyeColorRegistryModule() {
        super(DyeColors.class);
    }

    @Override
    public void registerDefaults() {
        Arrays.stream(LanternDyeColor.values()).forEach(this::register);
    }
}
