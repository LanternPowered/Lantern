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

import org.lanternpowered.api.util.optional.emptyOptionalDouble
import org.lanternpowered.api.util.optional.emptyOptionalInt
import org.lanternpowered.api.util.optional.emptyOptionalLong
import org.lanternpowered.api.util.optional.optionalDouble
import org.lanternpowered.api.util.optional.optionalInt
import org.lanternpowered.api.util.optional.optionalLong
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.data.value.ValueContainer
import java.util.Optional
import java.util.OptionalDouble
import java.util.OptionalInt
import java.util.OptionalLong
import java.util.function.Supplier

/**
 * The base class for all the [ValueContainer]s.
 */
@LocalDataDsl
interface ValueContainerBase : ValueContainer {

    @JvmDefault
    override fun getInt(key: Supplier<out Key<out Value<Int>>>): OptionalInt = getInt(key.get())

    @JvmDefault
    override fun getInt(key: Key<out Value<Int>>): OptionalInt =
            get(key).map { it.optionalInt() }.orElseGet { emptyOptionalInt() }

    @JvmDefault
    override fun getDouble(key: Supplier<out Key<out Value<Double>>>): OptionalDouble = getDouble(key.get())

    @JvmDefault
    override fun getDouble(key: Key<out Value<Double>>): OptionalDouble =
            get(key).map { it.optionalDouble() }.orElseGet { emptyOptionalDouble() }

    @JvmDefault
    override fun getLong(key: Supplier<out Key<out Value<Long>>>): OptionalLong = getLong(key.get())

    @JvmDefault
    override fun getLong(key: Key<out Value<Long>>): OptionalLong =
            get(key).map { it.optionalLong() }.orElseGet { emptyOptionalLong() }

    @JvmDefault
    override fun <E : Any> get(key: Supplier<out Key<out Value<E>>>): Optional<E> = get(key.get())

    @JvmDefault
    override fun <E : Any> get(key: Key<out Value<E>>): Optional<E>

    @JvmDefault
    override fun <E : Any, V : Value<E>> getValue(key: Supplier<out Key<V>>): Optional<V> = getValue(key.get())

    @JvmDefault
    override fun <E : Any, V : Value<E>> getValue(key: Key<V>): Optional<V>

    override fun getKeys(): Set<Key<*>>

    override fun getValues(): Set<Value.Immutable<*>>

    @JvmDefault
    override fun <E : Any> getOrElse(key: Supplier<out Key<out Value<E>>>, defaultValue: E): E = getOrElse(key.get(), defaultValue)

    @JvmDefault
    override fun <E : Any> getOrElse(key: Key<out Value<E>>, defaultValue: E): E = super.getOrElse(key, defaultValue)

    @JvmDefault
    override fun <E : Any> getOrNull(key: Supplier<out Key<out Value<E>>>): E? = getOrNull(key.get())

    @JvmDefault
    override fun <E : Any> getOrNull(key: Key<out Value<E>>): E? = super.getOrNull(key)

    @JvmDefault
    override fun supports(key: Supplier<out Key<*>>): Boolean = supports(key.get())

    override fun supports(key: Key<*>): Boolean

    @JvmDefault
    override fun supports(value: Value<*>) = super.supports(value)

    @JvmDefault
    override fun <E : Any> require(key: Supplier<out Key<out Value<E>>>): E = require(key.get())

    @JvmDefault
    override fun <E : Any> require(key: Key<out Value<E>>): E = super.require(key)
}
