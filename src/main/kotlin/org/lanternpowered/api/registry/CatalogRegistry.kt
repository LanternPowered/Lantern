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

import org.lanternpowered.api.Lantern
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.CatalogType
import org.spongepowered.api.registry.UnknownTypeException
import java.util.function.Supplier
import kotlin.reflect.KClass

/**
 * Attempts to retrieve the specific type of [CatalogType] based on
 * the key given and throws an exception if not type was found.
 *
 * <p>Some types may not be available for various reasons including but not
 * restricted to: mods adding custom types, plugins providing custom types,
 * game version changes.</p>
 *
 * @param key The catalog key
 * @param T The catalog type
 * @return The found type, if available
 * @see CatalogType
 */
inline fun <reified T : CatalogType> CatalogRegistry.require(key: CatalogKey): T = require(T::class, key)

/**
 * Attempts to retrieve the specific type of {@link CatalogType} based on
 * the key given.
 *
 * <p>Some types may not be available for various reasons including but not
 * restricted to: mods adding custom types, plugins providing custom types,
 * game version changes.</p>
 *
 * @param key The catalog key
 * @param T The catalog type
 * @return The found type, if available
 * @see CatalogType
 */
inline operator fun <reified T : CatalogType> CatalogRegistry.get(key: CatalogKey): T? = get(T::class, key)

/**
 * Creates a [Supplier] that will be used to get [CatalogType] instances.
 *
 * @param suggestedId The suggested id to use
 * @param T The type of catalog
 * @return The supplier
 * @throws UnknownTypeException If the type provided has not been registered
 */
inline fun <reified T : CatalogType> CatalogRegistry.provideSupplier(suggestedId: String): Supplier<T> = provideSupplier(T::class, suggestedId)

/**
 * Gets a collection of all available found specific types of
 * [CatalogType] requested.
 *
 * The presented [CatalogType]s may not exist in default catalogs
 * due to various reasons including but not restricted to: mods, plugins,
 * game changes.
 *
 * @param T The type of [CatalogType]
 * @return A collection of all known types of the requested catalog type
 */
inline fun <reified T : CatalogType> CatalogRegistry.getAllOf(): Sequence<T> = getAllOf(T::class)

/**
 * The catalog registry.
 */
interface CatalogRegistry : org.spongepowered.api.registry.CatalogRegistry {

    /**
     * Creates a [Supplier] that will be used to get [CatalogType] instances.
     *
     * @param catalogClass The catalog class
     * @param suggestedId The suggested id to use
     * @param T The type of catalog
     * @param E The generic of the catalog (if applicable)
     * @return The supplier
     * @throws UnknownTypeException If the type provided has not been registered
     */
    fun <T : CatalogType, E : T> provideSupplier(catalogClass: KClass<T>, suggestedId: String): Supplier<E>

    /**
     * Attempts to retrieve the specific type of [CatalogType] based on
     * the key given.
     *
     * <p>Some types may not be available for various reasons including but not
     * restricted to: mods adding custom types, plugins providing custom types,
     * game version changes.</p>
     *
     * @param typeClass The class of the type of [CatalogType]
     * @param key The catalog key
     * @param T The catalog type
     * @return The found type, if available
     * @see CatalogType
     */
    fun <T : CatalogType> get(typeClass: KClass<T>, key: CatalogKey): T?

    /**
     * Attempts to retrieve the specific type of [CatalogType] based on
     * the key given and throws an exception if not type was found.
     *
     * <p>Some types may not be available for various reasons including but not
     * restricted to: mods adding custom types, plugins providing custom types,
     * game version changes.</p>
     *
     * @param typeClass The class of the type of [CatalogType]
     * @param key The catalog key
     * @param T The catalog type
     * @return The found type, if available
     * @see CatalogType
     */
    fun <T : CatalogType> require(typeClass: KClass<T>, key: CatalogKey): T =
            get(typeClass, key) ?: throw IllegalArgumentException("Can't find a ${typeClass.simpleName} with the key: $key")

    /**
     * Gets a collection of all available found specific types of
     * [CatalogType] requested.
     *
     * The presented [CatalogType]s may not exist in default catalogs
     * due to various reasons including but not restricted to: mods, plugins,
     * game changes.
     *
     * @param typeClass The class of [CatalogType]
     * @param T The type of [CatalogType]
     * @return A collection of all known types of the requested catalog type
     */
    fun <T : CatalogType> getAllOf(typeClass: KClass<T>): Sequence<T>

    /**
     * The singleton instance of the catalog registry.
     */
    companion object : CatalogRegistry by Lantern.registry.catalogRegistry
}
