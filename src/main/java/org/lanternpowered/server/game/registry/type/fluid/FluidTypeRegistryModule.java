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
package org.lanternpowered.server.game.registry.type.fluid;

import org.lanternpowered.api.catalog.CatalogKeys;
import org.lanternpowered.server.fluid.LanternFluidType;
import org.lanternpowered.server.game.registry.InternalPluginCatalogRegistryModule;
import org.spongepowered.api.fluid.FluidType;
import org.spongepowered.api.fluid.FluidTypes;

import java.util.Collections;

public class FluidTypeRegistryModule extends InternalPluginCatalogRegistryModule<FluidType> {

    private static final FluidTypeRegistryModule INSTANCE = new FluidTypeRegistryModule();

    public static FluidTypeRegistryModule get() {
        return INSTANCE;
    }

    private FluidTypeRegistryModule() {
        super(FluidTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternFluidType(CatalogKeys.minecraft("water", "Water"), Collections.emptyList()));
        register(new LanternFluidType(CatalogKeys.minecraft("lava", "Lava"), Collections.emptyList()));
    }
}
