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

interface InternalCatalogTypeRegistryBuilder<T : CatalogType, I> : CatalogTypeRegistryBuilder<T> {

    /**
     * Registers a new [CatalogType] with the given [internalId].
     */
    fun <R : T> register(internalId: I, type: R): R
}
