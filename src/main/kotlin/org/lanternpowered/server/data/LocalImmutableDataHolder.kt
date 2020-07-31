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
package org.lanternpowered.server.data

import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.util.optional.asOptional
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
                return if (value == ValueCache.None) emptyOptional() else value.uncheckedCast<V>().asOptional()
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
