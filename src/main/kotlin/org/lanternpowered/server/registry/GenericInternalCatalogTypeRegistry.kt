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
 * Constructs a new [GenericInternalCatalogTypeRegistry].
 */
inline fun <reified T : CatalogType, I> customInternalCatalogTypeRegistry(noinline fn: GenericInternalCatalogTypeRegistryBuilder<T, I>.() -> Unit):
        GenericInternalCatalogTypeRegistry<T, I> = customInternalCatalogTypeRegistry(typeTokenOf(), fn)

/**
 * Constructs a new [GenericInternalCatalogTypeRegistry].
 */
fun <T : CatalogType, I> customInternalCatalogTypeRegistry(typeToken: TypeToken<T>, fn: GenericInternalCatalogTypeRegistryBuilder<T, I>.() -> Unit):
        GenericInternalCatalogTypeRegistry<T, I> = LanternCatalogTypeRegistryFactory.buildGeneric(typeToken, fn)

interface GenericInternalCatalogTypeRegistry<T : CatalogType, I> : CatalogTypeRegistry<T> {

    /**
     * Gets the id for the given catalog type.
     */
    fun getId(type: T): I?

    /**
     * Attempts to get a id for the given type. Throws an
     * [IllegalArgumentException] if the id couldn't be found.
     */
    fun requireId(type: T): I = getId(type) ?: throw IllegalArgumentException(
            "Can't find an internal id for the type: $type")

    /**
     * Attempts to get a type for the given [id].
     */
    fun get(id: I): T?

    /**
     * Attempts to get a type for the given [id]. Throws an
     * [IllegalArgumentException] if the id couldn't be found.
     */
    fun require(id: I): T = get(id) ?: throw IllegalArgumentException(
            "Can't find a ${typeToken.rawType.simpleName} with the id: $id")
}
