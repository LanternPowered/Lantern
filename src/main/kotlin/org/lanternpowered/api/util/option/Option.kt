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
import org.lanternpowered.api.util.TypeToken
import kotlin.reflect.KClass

/**
 * Represents a option.
 *
 * @constructor Constructs and registers a new [Option]
 * @property defaultValue The default option value
 * @property type The type of option value
 * @param T The option map type this option targets
 * @param V The value type of this option
 */
data class Option<T : OptionMapType, V>(
        val key: CatalogKey,
        val type: TypeToken<V>,
        val defaultValue: V,
        val mapType: Class<T>
) {

    constructor(key: CatalogKey, type: TypeToken<V>, defaultValue: V, mapType: KClass<T>) :
            this(key, type, defaultValue, mapType.java)

    init {
        val registry = OptionMapRegistry.of(this.mapType)
        // Each key per map type should be unique
        check(this.key !in registry) {
            "There is already a option with id '$key' registered for map type '${mapType.name}'." }
        // Register the key
        registry.byKey[this.key] = this
    }
}
