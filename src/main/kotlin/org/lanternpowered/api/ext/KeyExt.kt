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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.ext

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.data.KeyBuilder
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value

/**
 * Constructs a new [Key] with the given [CatalogKey] and value [TypeToken].
 */
fun <V : Value<*>> valueKeyOf(key: CatalogKey, valueType: TypeToken<V>, fn: KeyBuilder<V>.() -> Unit = {}): Key<V> =
        builderOf<KeyBuilder<V>>().key(key).type(valueType).requireExplicitRegistration().apply(fn).build()

/**
 * Constructs a new [Key] with the given [CatalogKey] and value type [V].
 */
inline fun <reified V : Value<*>> valueKeyOf(key: CatalogKey, fn: KeyBuilder<V>.() -> Unit = {}): Key<V> =
        builderOf<KeyBuilder<V>>().key(key).type(typeTokenOf<V>()).requireExplicitRegistration().apply(fn).build()
