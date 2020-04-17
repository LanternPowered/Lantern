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

import org.lanternpowered.api.ext.*
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.MapValue
import java.util.function.Function
import java.util.function.Predicate

class LanternMutableMapValue<K, V>(key: Key<out MapValue<K, V>>, value: MutableMap<K, V>) : LanternMapValue<K, V>(key, value), MapValue.Mutable<K, V> {

    override fun put(key: K, value: V) = apply { this.value[key] = value }

    override fun putAll(map: Map<K, V>) = apply { this.value.putAll(map) }

    override fun remove(key: K) = apply { this.value.remove(key) }

    override fun removeAll(keys: Iterable<K>) = apply { this.value.removeAll(keys) }

    override fun removeAll(predicate: Predicate<Map.Entry<K, V>>) = apply { this.value.entries.removeIf(predicate) }

    override fun set(value: MutableMap<K, V>) = apply { this.value = value }

    override fun transform(function: Function<MutableMap<K, V>, MutableMap<K, V>>) = set(function.apply(get()))

    override fun copy() = LanternMutableMapValue(this.key, CopyHelper.copyMap(this.value))

    override fun asImmutable(): MapValue.Immutable<K, V> = this.key.valueConstructor.getImmutable(this.value).asImmutable()
}
