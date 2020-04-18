/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.api.registry

import org.lanternpowered.api.Lantern
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.CatalogType
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
