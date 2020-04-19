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

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.api.util.ToStringHelper;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.world.World;
import org.spongepowered.api.world.biome.BiomeGenerationSettings;
import org.spongepowered.api.world.biome.BiomeType;
import org.spongepowered.api.world.biome.VirtualBiomeType;

import java.util.function.Function;

public class LanternVirtualBiomeType extends AbstractBiomeType implements VirtualBiomeType {

    private final Function<World, BiomeGenerationSettings> settingsFunction;
    private final BiomeType persistedType;

    LanternVirtualBiomeType(CatalogKey key, Function<World, BiomeGenerationSettings> settingsFunction, BiomeType persistedType) {
        super(key);
        this.settingsFunction = settingsFunction;
        this.persistedType = persistedType;
    }

    @Override
    public BiomeType getPersistedType() {
        return this.persistedType;
    }

    @Override
    public ToStringHelper toStringHelper() {
        return super.toStringHelper().add("persistedType", this.persistedType);
    }

    @Override
    public BiomeGenerationSettings createDefaultGenerationSettings(World world) {
        checkNotNull(world, "world");
        return this.settingsFunction.apply(world);
    }
}
