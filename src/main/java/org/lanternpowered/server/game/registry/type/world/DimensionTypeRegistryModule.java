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
package org.lanternpowered.server.game.registry.type.world;

import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule;
import org.lanternpowered.server.world.dimension.LanternDimensionEnd;
import org.lanternpowered.server.world.dimension.LanternDimensionNether;
import org.lanternpowered.server.world.dimension.LanternDimensionOverworld;
import org.lanternpowered.server.world.dimension.LanternDimensionType;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.world.DimensionType;
import org.spongepowered.api.world.DimensionTypes;
import org.spongepowered.api.world.gen.GeneratorTypes;

@RegistrationDependency(GeneratorTypeRegistryModule.class)
public class DimensionTypeRegistryModule extends AdditionalPluginCatalogRegistryModule<DimensionType> {

    public DimensionTypeRegistryModule() {
        super(DimensionTypes.class);
    }

    @Override
    public void registerDefaults() {
        register(new LanternDimensionType<>(ResourceKey.minecraft("nether"), "Nether", -1, LanternDimensionNether.class,
                GeneratorTypes.NETHER, true, true, false, false, LanternDimensionNether::new));
        register(new LanternDimensionType<>(ResourceKey.minecraft("overworld"), "Overworld", 0, LanternDimensionOverworld.class,
                GeneratorTypes.OVERWORLD, true, false, true, true, LanternDimensionOverworld::new));
        register(new LanternDimensionType<>(ResourceKey.minecraft("the_end"), "The End", 1, LanternDimensionEnd.class,
                GeneratorTypes.THE_END, true, false, false, false, LanternDimensionEnd::new));
    }
}
