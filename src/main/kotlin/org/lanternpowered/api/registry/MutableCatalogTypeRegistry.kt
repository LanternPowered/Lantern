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
interface MutableCatalogTypeRegistry<T : CatalogType> : CatalogTypeRegistry<T>,
        MutableCatalogTypeRegistryBase<T, CatalogTypeRegistryBuilder<T>, MutableCatalogTypeRegistry<T>>

/**
 * A mutable registry for catalog types.
 */
interface MutableCatalogTypeRegistryBase<T, B, R> : CatalogTypeRegistry<T>
        where T : CatalogType,
              B : CatalogTypeRegistryBuilder<T>,
              R : MutableCatalogTypeRegistryBase<T, B, R> {

    /**
     * Watches the registry for changes. Watchers will be triggered
     * after every [load].
     */
    fun watch(watcher: (registry: R) -> Unit)

    /**
     * Loads or reloads the [MutableCatalogTypeRegistry]. This clears the
     * registry and populates the registry again. This is the only way
     * to add or remove entries.
     */
    fun load(fn: B.() -> Unit)
}
