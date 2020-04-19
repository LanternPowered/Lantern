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
package org.lanternpowered.server.game.registry

import org.spongepowered.api.CatalogType

@Deprecated(message = "TODO")
interface AlternateCatalogRegistryModule<T : CatalogType> : CatalogRegistryModule<T> {

    /**
     * Gets the catalog [map][Map] instead of defaulting to utilizing
     * [RegisterCatalog] annotated field for the map of catalog types.
     *
     * @return The catalog map to use for the registry system
     */
    fun provideCatalogMap(): Map<String, T>
}
