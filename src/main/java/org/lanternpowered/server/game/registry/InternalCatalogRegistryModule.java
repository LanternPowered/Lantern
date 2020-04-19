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
package org.lanternpowered.server.game.registry;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.registry.CatalogRegistryModule;

import java.util.Optional;

public interface InternalCatalogRegistryModule<T extends CatalogType> extends CatalogRegistryModule<T> {

    /**
     * Gets the {@link T} by using the internal id.
     *
     * @param internalId The internal id
     * @return The catalog type if present
     */
    Optional<T> getByInternalId(int internalId);
}
