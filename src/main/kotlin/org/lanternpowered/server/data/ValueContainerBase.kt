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

import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.data.value.ValueContainer
import java.util.Optional
import java.util.OptionalDouble
import java.util.OptionalInt
import java.util.OptionalLong

/**
 * The base class for all the [ValueContainer]s.
 */
@LocalDataDsl
interface ValueContainerBase : ValueContainer {

    @JvmDefault
    override fun getInt(key: Key<out Value<Int>>): OptionalInt =
            get(key).map { OptionalInt.of(it) }.orElseGet { OptionalInt.empty() }

    @JvmDefault
    override fun getDouble(key: Key<out Value<Double>>): OptionalDouble =
            get(key).map<OptionalDouble> { OptionalDouble.of(it) }.orElseGet { OptionalDouble.empty() }

    @JvmDefault
    override fun getLong(key: Key<out Value<Long>>): OptionalLong =
            get(key).map<OptionalLong> { OptionalLong.of(it) }.orElseGet { OptionalLong.empty() }

    override fun <E : Any> get(key: Key<out Value<E>>): Optional<E>

    override fun <E : Any, V : Value<E>> getValue(key: Key<V>): Optional<V>

    override fun getKeys(): Set<Key<*>>

    override fun getValues(): Set<Value.Immutable<*>>

    @JvmDefault
    override fun <E : Any> getOrElse(key: Key<out Value<E>>, defaultValue: E): E = super.getOrElse(key, defaultValue)

    @JvmDefault
    override fun <E : Any> getOrNull(key: Key<out Value<E>>): E? = super.getOrNull(key)

    override fun supports(key: Key<*>): Boolean

    @JvmDefault
    override fun supports(value: Value<*>) = super.supports(value)

    @JvmDefault
    override fun <E : Any> require(key: Key<out Value<E>>): E = super.require(key)

    override fun copy(): ValueContainer
}
