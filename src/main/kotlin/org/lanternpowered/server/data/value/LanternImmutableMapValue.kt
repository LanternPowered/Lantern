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

import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.MapValue
import java.util.function.Function
import java.util.function.Predicate

class LanternImmutableMapValue<K, V>(key: Key<out MapValue<K, V>>, value: MutableMap<K, V>) :
        LanternMapValue<K, V>(key, value), MapValue.Immutable<K, V> {

    override fun get() = CopyHelper.copyMap(super.get())

    private fun withValue(value: MutableMap<K, V>): MapValue.Immutable<K, V> = this.key.valueConstructor.getRawImmutable(value).asImmutable()

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
