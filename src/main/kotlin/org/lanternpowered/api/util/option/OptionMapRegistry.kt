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
@file:Suppress("UNCHECKED_CAST")

package org.lanternpowered.api.util.option

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.util.collections.toImmutableList
import java.util.concurrent.ConcurrentHashMap

/**
 * The registry for a specific [OptionMap] subclass.
 *
 * @property mapType The map type this registry holds options for
 */
class OptionMapRegistry<T : OptionMapType> internal constructor(val mapType: Class<T>) {

    // All the options mapped by their key
    internal val byKey = HashMap<CatalogKey, Option<T, *>>()

    /**
     * Gets all the options for the target map type.
     */
    fun all(): Collection<Option<T, *>> = this.byKey.values.toImmutableList()

    /**
     * Gets whether the given [CatalogKey] is present within this registry.
     */
    operator fun contains(key: CatalogKey): Boolean = this.byKey.containsKey(key)

    /**
     * Gets whether the given [Option] is present within this registry.
     */
    operator fun contains(option: Option<T, *>): Boolean = this.byKey.containsValue(option)

    /**
     * Gets the [Option] that is registered for the given [CatalogKey].
     */
    operator fun get(key: CatalogKey): Option<T, *>? = this.byKey[key]

    companion object {

        private val byMapType = ConcurrentHashMap<Class<*>, OptionMapRegistry<*>>()

        /**
         * Gets or constructs the [OptionMapRegistry] for the given [OptionMap] type.
         */
        fun <T : OptionMapType> of(mapType: Class<T>): OptionMapRegistry<T> =
                this.byMapType.computeIfAbsent(mapType) { OptionMapRegistry(mapType) } as OptionMapRegistry<T>
    }
}
