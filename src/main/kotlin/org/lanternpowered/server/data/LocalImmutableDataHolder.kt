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
package org.lanternpowered.server.data

import org.lanternpowered.api.ext.*
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import java.util.Optional

interface LocalImmutableDataHolder<I : DataHolder.Immutable<I>> : LocalDataHolder, DataHolder.Immutable<I>, ImmutableDataHolder<I> {

    override val keyRegistry: LocalKeyRegistry<out LocalImmutableDataHolder<I>>

    /**
     * Gets a cache that can be used to cache the retrieved
     * [Value]s. Returns `null` by default which means that
     * caching is disabled.
     */
    @JvmDefault
    val valueCache: ValueCache? get() = null

    @JvmDefault
    override fun <E : Any, V : Value<E>> getValue(key: Key<V>): Optional<V> {
        val valueCache = this.valueCache
        if (valueCache != null) {
            var value = valueCache.values[key.uncheckedCast()]
            if (value != null) {
                return if (value == ValueCache.None) emptyOptional() else value.uncheckedCast<V>().optional()
            }
            value = super<LocalDataHolder>.getValue(key).map { it.asImmutable().uncheckedCast<V>() }
            valueCache.values[key] = value.uncheckedCast<Optional<Any>>().orElse(ValueCache.None)
            return value
        }
        return super<LocalDataHolder>.getValue(key).map { value -> value.asImmutable().uncheckedCast<V>() }
    }

    @JvmDefault
    override fun with(value: Value<*>): Optional<I> {
        val key = value.key

        // Check for a global registration
        val localRegistration = this.keyRegistry[key.uncheckedCast<Key<Value<Any>>>()]
        if (localRegistration != null) {
            return localRegistration.dataProvider<Value<Any>, Any>().withValue(uncheckedCast(), value.uncheckedCast())
        }

        return super<ImmutableDataHolder>.with(value)
    }

    @JvmDefault
    override fun <E : Any> with(key: Key<out Value<E>>, value: E): Optional<I> {
        // Check the local key registration
        val localRegistration = this.keyRegistry[key]
        if (localRegistration != null) {
            return localRegistration.dataProvider<Value<E>, E>().with(uncheckedCast(), value)
        }

        return super<ImmutableDataHolder>.with(key, value)
    }

    @JvmDefault
    override fun without(key: Key<*>): Optional<I> {
        // Check for a global registration
        val localRegistration = this.keyRegistry[key.uncheckedCast<Key<Value<Any>>>()]
        if (localRegistration != null) {
            return localRegistration.dataProvider<Value<Any>, Any>().without(uncheckedCast())
        }

        return super<ImmutableDataHolder>.without(key)
    }

    class ValueCache {

        // A object that represents that the container isn't present on the holder,
        // null means that it wasn't being retrieved before
        internal object None

        // The cached values
        internal val values = mutableMapOf<Key<*>, Any>()
    }
}
