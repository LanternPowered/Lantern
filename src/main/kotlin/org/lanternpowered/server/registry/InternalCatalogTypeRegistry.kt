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
import org.lanternpowered.api.registry.CatalogTypeRegistry
import org.lanternpowered.api.util.type.TypeToken
import org.lanternpowered.api.util.type.typeTokenOf

/**
 * Constructs a new [InternalCatalogTypeRegistry].
 */
inline fun <reified T : CatalogType> internalCatalogTypeRegistry(noinline fn: InternalCatalogTypeRegistryBuilder<T>.() -> Unit):
        InternalCatalogTypeRegistry<T> = internalCatalogTypeRegistry(typeTokenOf(), fn)

/**
 * Constructs a new [InternalCatalogTypeRegistry].
 */
fun <T : CatalogType> internalCatalogTypeRegistry(typeToken: TypeToken<T>, fn: InternalCatalogTypeRegistryBuilder<T>.() -> Unit):
        InternalCatalogTypeRegistry<T> = LanternCatalogTypeRegistryFactory.build(typeToken, fn)

interface InternalCatalogTypeRegistry<T : CatalogType> : CatalogTypeRegistry<T> {

    /**
     * Gets the [InternalCatalogId] for the given catalog type.
     */
    fun getId(type: T): InternalCatalogId

    /**
     * Attempts to get a type for the given [id].
     */
    fun get(id: InternalCatalogId): T?

    /**
     * Attempts to get a type for the given [id]. Throws an
     * [IllegalArgumentException] if the id couldn't be found.
     */
    fun require(id: InternalCatalogId): T = get(id) ?: throw IllegalArgumentException(
            "Can't find a ${typeToken.rawType.simpleName} with the id: ${id.value}")
}
