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
package org.lanternpowered.server.data.value

import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.MapValue
import java.util.function.Function
import java.util.function.Predicate

class LanternImmutableMapValue<K, V>(key: Key<out MapValue<K, V>>, value: MutableMap<K, V>) :
        LanternMapValue<K, V>(key, value), MapValue.Immutable<K, V> {

    private fun withValue(value: MutableMap<K, V>) = LanternImmutableMapValue(this.key, value)

    override fun with(key: K, value: V): MapValue.Immutable<K, V> {
        val map = get()
        map[key] = value
        return withValue(map)
    }

    override fun withAll(map: Map<K, V>): MapValue.Immutable<K, V> {
        val value = get()
        value.putAll(map)
        return withValue(value)
    }

    override fun without(key: K): MapValue.Immutable<K, V> {
        if (key !in this.value) {
            return this
        }
        val map = get()
        map.remove(key)
        return withValue(map)
    }

    override fun withoutAll(keys: Iterable<K>): MapValue.Immutable<K, V> {
        val map = get()
        keys.forEach { map.remove(it) }
        return withValue(map)
    }

    override fun withoutAll(predicate: Predicate<Map.Entry<K, V>>): MapValue.Immutable<K, V> {
        val map = get()
        map.entries.removeIf(predicate)
        return withValue(map)
    }

    override fun with(value: MutableMap<K, V>) = withValue(CopyHelper.copy(value))

    override fun transform(function: Function<Map<K, V>, MutableMap<K, V>>) = with(function.apply(get()))

    override fun asMutable() = LanternMutableMapValue(this.key, get())
}
