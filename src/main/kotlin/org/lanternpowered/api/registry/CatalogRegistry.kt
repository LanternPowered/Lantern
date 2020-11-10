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

import org.spongepowered.api.CatalogType
import org.lanternpowered.api.key.NamespacedKey
import org.spongepowered.api.registry.UnknownTypeException
import java.util.function.Supplier
import kotlin.collections.Collection
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
inline fun <reified T : CatalogType> CatalogRegistry.require(key: NamespacedKey): T = this.require(T::class, key)

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
inline operator fun <reified T : CatalogType> CatalogRegistry.get(key: NamespacedKey): T? = this.get(T::class, key)

/**
 * Provides the [T] that will be used to get [CatalogType] instances.
 *
 * This method isn't supported by [MutableCatalogTypeRegistry]s, using this for
 * that registry will result in an [UnsupportedOperationException]. Since there's
 * no guarantee that every time the same instance will be returned.
 *
 * @param suggestedId The suggested id to use
 * @param T The type of catalog
 * @return The supplier
 * @throws UnknownTypeException If the type provided has not been registered
 * @throws UnsupportedOperationException If the registry is a mutable registry
 */
inline fun <reified T : CatalogType> CatalogRegistry.provide(suggestedId: String): CatalogTypeProvider<T> = this.provide(T::class, suggestedId)

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
inline fun <reified T : CatalogType> CatalogRegistry.getAllOf(): Collection<T> = this.getAllOf(T::class)

/**
 * The catalog registry.
 */
interface CatalogRegistry : org.spongepowered.api.registry.CatalogRegistry {

    @Deprecated(
            message = "Use provide instead.",
            level = DeprecationLevel.WARNING,
            replaceWith = ReplaceWith("provide(catalogClass, suggestedId)")
    )
    override fun <T : CatalogType, E : T> provideSupplier(catalogClass: Class<T>, suggestedId: String): Supplier<E> =
            this.provide(catalogClass.kotlin, suggestedId)

    /**
     * Creates a [CatalogTypeProvider] that will be used to get [CatalogType] instances.
     *
     * @param catalogClass The catalog class
     * @param suggestedId The suggested id to use
     * @param T The type of catalog
     * @param E The generic of the catalog (if applicable)
     * @return The supplier
     * @throws UnknownTypeException If the type provided has not been registered
     */
    fun <T : CatalogType, E : T> provide(catalogClass: KClass<T>, suggestedId: String): CatalogTypeProvider<E>

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
    fun <T : CatalogType> get(typeClass: KClass<T>, key: NamespacedKey): T?

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
    fun <T : CatalogType> require(typeClass: KClass<T>, key: NamespacedKey): T =
            this.get(typeClass, key) ?: throw IllegalArgumentException("Can't find a ${typeClass.simpleName} with the key: $key")

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
    fun <T : CatalogType> getAllOf(typeClass: KClass<T>): Collection<T>

    /**
     * The singleton instance of the catalog registry.
     */
    companion object : CatalogRegistry by GameRegistry.catalogRegistry
}
