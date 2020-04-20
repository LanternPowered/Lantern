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
package org.lanternpowered.api.registry

import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.util.type.TypeToken
import org.lanternpowered.api.util.type.typeTokenOf

/**
 * Constructs a new [MutableCatalogTypeRegistry].
 */
inline fun <reified T : CatalogType> mutableCatalogTypeRegistry():
        MutableCatalogTypeRegistry<T> = mutableCatalogTypeRegistry(typeTokenOf())

/**
 * Constructs a new [MutableCatalogTypeRegistry].
 */
fun <T : CatalogType> mutableCatalogTypeRegistry(typeToken: TypeToken<T>):
        MutableCatalogTypeRegistry<T> = factoryOf<CatalogTypeRegistry.Factory>().buildMutable(typeToken)

/**
 * A mutable registry for catalog types.
 */
interface MutableCatalogTypeRegistry<T : CatalogType> : CatalogTypeRegistry<T> {

    /**
     * Loads or reloads the [MutableCatalogTypeRegistry]. This clears the
     * registry and populates the registry again. This is the only way
     * to remove entries.
     */
    fun load(fn: CatalogTypeRegistryBuilder<T>.() -> Unit)
}
