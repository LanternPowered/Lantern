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
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.type.TypeToken
import org.lanternpowered.api.util.type.typeTokenOf
import java.util.Optional

/**
 * Constructs a new [InternalCatalogTypeRegistry].
 */
inline fun <reified T : CatalogType> internalCatalogTypeRegistryOfArray(noinline provider: () -> Array<out T>):
        InternalCatalogTypeRegistry<T> = internalCatalogTypeRegistryOf { provider().asList() }

/**
 * Constructs a new [InternalCatalogTypeRegistry].
 */
inline fun <reified T : CatalogType> internalCatalogTypeRegistryOf(noinline provider: () -> Iterable<T>):
        InternalCatalogTypeRegistry<T> = internalCatalogTypeRegistry<T>(typeTokenOf()) {
    for (value in provider())
        register(value)
}

/**
 * Constructs a new [InternalCatalogTypeRegistry].
 */
inline fun <reified T : CatalogType> internalCatalogTypeRegistry(noinline fn: InternalCatalogTypeRegistryBuilder<T, Int>.() -> Unit):
        InternalCatalogTypeRegistry<T> = internalCatalogTypeRegistry(typeTokenOf(), fn)

/**
 * Constructs a new [InternalCatalogTypeRegistry].
 */
fun <T : CatalogType> internalCatalogTypeRegistry(typeToken: TypeToken<T>, fn: InternalCatalogTypeRegistryBuilder<T, Int>.() -> Unit):
        InternalCatalogTypeRegistry<T> = LanternCatalogTypeRegistryFactory.build(typeToken, fn)

/**
 * A catalog type registry with int based internal mappings.
 */
interface InternalCatalogTypeRegistry<T : CatalogType> : CatalogTypeRegistry<T> {

    /**
     * Gets the id for the given catalog type.
     */
    fun getId(type: T): Int

    /**
     * Attempts to get a type for the given [id].
     */
    fun get(id: Int): T?

    /**
     * Attempts to get a type for the given [id].
     */
    fun getOptional(id: Int): Optional<T> = get(id).asOptional()

    /**
     * Attempts to get a type for the given [id]. Throws an
     * [IllegalArgumentException] if the id couldn't be found.
     */
    fun require(id: Int): T = get(id) ?: throw IllegalArgumentException(
            "Can't find a ${typeToken.rawType.simpleName} with the id: $id")
}
