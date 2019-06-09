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
package org.lanternpowered.api.data

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.util.builder.CatalogBuilder
import org.spongepowered.api.data.value.Value

typealias Key<V> = org.spongepowered.api.data.Key<V>

/**
 * A builder class for [Key]s.
 */
interface KeyBuilder<V : Value<*>> : CatalogBuilder<Key<V>, KeyBuilder<V>> {

    /**
     * Starter method for the builder. This defines the generics for the
     * builder itself to provide the properly generified [Key].
     *
     * @param token The type token, preferably an anonymous
     * @param <T> The element type of the Key
     * @param <B> The base value type of the key
     * @return This builder, generified
     */
    fun <N : Value<*>> type(token: TypeToken<N>): KeyBuilder<N>

    /**
     * Enables the requirement that the key is registered explicitly on a value collection.
     *
     * @return This builder, for chaining
     */
    fun requireExplicitRegistration(): KeyBuilder<V>
}
