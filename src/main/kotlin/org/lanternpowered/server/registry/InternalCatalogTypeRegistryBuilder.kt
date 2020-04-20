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
package org.lanternpowered.server.registry

import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.registry.CatalogTypeRegistryBuilder

interface InternalCatalogTypeRegistryBuilder<T : CatalogType> : CatalogTypeRegistryBuilder<T> {

    /**
     * Registers a new [CatalogType] with the given [internalId].
     */
    fun register(internalId: Int, type: T) = register(InternalCatalogId(internalId), type)

    /**
     * Registers a new [CatalogType] with the given [internalId].
     */
    fun register(internalId: InternalCatalogId, type: T)
}
