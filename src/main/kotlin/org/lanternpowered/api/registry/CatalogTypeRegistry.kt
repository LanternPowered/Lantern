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

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.catalog.CatalogType
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.api.util.type.TypeToken
import org.lanternpowered.api.util.type.typeTokenOf
import java.util.Optional
import java.util.function.Supplier

/**
 * Constructs a new [CatalogTypeRegistry].
 */
inline fun <reified T : CatalogType> catalogTypeRegistryOf(noinline values: () -> Iterable<T>): CatalogTypeRegistry<T> =
        catalogTypeRegistryOf(typeTokenOf(), values)

/**
 * Constructs a new [CatalogTypeRegistry].
 */
fun <T : CatalogType> catalogTypeRegistryOf(typeToken: TypeToken<T>, values: () -> Iterable<T>): CatalogTypeRegistry<T> =
        catalogTypeRegistry(typeToken) {
            for (value in values())
                register(value)
        }

/**
 * Constructs a new [CatalogTypeRegistry].
 */
inline fun <reified T : CatalogType> catalogTypeRegistry(noinline fn: CatalogTypeRegistryBuilder<T>.() -> Unit): CatalogTypeRegistry<T> =
        catalogTypeRegistry(typeTokenOf(), fn)

/**
 * Constructs a new [CatalogTypeRegistry].
 */
fun <T : CatalogType> catalogTypeRegistry(typeToken: TypeToken<T>, fn: CatalogTypeRegistryBuilder<T>.() -> Unit): CatalogTypeRegistry<T> =
        factoryOf<CatalogTypeRegistry.Factory>().build(typeToken, fn)

/**
 * A registry for catalog types.
 */
interface CatalogTypeRegistry<T : CatalogType> : Iterable<T> {

    override fun iterator(): Iterator<T> = this.all.iterator()

    /**
     * The type token of the base catalog type.
     */
    val typeToken: TypeToken<T>

    /**
     * A collection with all the registered [CatalogType]s.
     */
    val all: Collection<T>

    /**
     * Attempts to get a type for the given [key].
     */
    operator fun get(key: CatalogKey): T?

    /**
     * Attempts to get a type for the given [key].
     */
    fun getOptional(key: CatalogKey): Optional<T> = get(key).optional()

    /**
     * Attempts to get a type for the given [key]. Throws an
     * [IllegalArgumentException] if the key couldn't be found.
     */
    fun require(key: CatalogKey): T = get(key) ?: throw IllegalArgumentException(
            "Can't find a ${typeToken.rawType.simpleName} with the key: $key")

    /**
     * Gets a [Supplier] for a type with the suggested id. The suggested
     * id doesn't contain a namespace. By default, the priority of namespaces
     * is the following: `minecraft`, `lantern`, `sponge`, plugin namespaces
     */
    fun provideSupplier(suggestedId: String): Supplier<T>

    interface Factory {

        fun <T : CatalogType> build(typeToken: TypeToken<T>, fn: CatalogTypeRegistryBuilder<T>.() -> Unit): CatalogTypeRegistry<T>

        fun <T : CatalogType> buildMutable(typeToken: TypeToken<T>): MutableCatalogTypeRegistry<T>
    }
}
