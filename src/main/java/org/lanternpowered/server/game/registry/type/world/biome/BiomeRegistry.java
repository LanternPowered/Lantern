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
package org.lanternpowered.server.game.registry.type.world.biome;

import org.spongepowered.api.registry.CatalogRegistryModule;
import org.spongepowered.api.world.biome.BiomeType;

import java.util.Optional;

public interface BiomeRegistry extends CatalogRegistryModule<BiomeType> {

    /**
     * Registers a default {@link BiomeType} with a specific internal id.
     *
     * @param internalId the internal id
     * @param biomeType the biome type
     */
    void register(byte internalId, BiomeType biomeType);

    /**
     * Registers a new {@link BiomeType}.
     *
     * @param biomeType the biome type
     */
    <A extends BiomeType> A register(A biomeType);

    /**
     * Gets the internal id of the biome type.
     *
     * @param biomeType the biome type
     * @return the internal id
     */
    short getInternalId(BiomeType biomeType);

    /**
     * Gets a biome type by using it's internal id.
     *
     * @param internalId the internal id
     * @return the biome type
     */
    Optional<BiomeType> getByInternalId(int internalId);

}
