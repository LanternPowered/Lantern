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
package org.lanternpowered.server.data.value

import org.lanternpowered.api.util.collections.removeAll
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
