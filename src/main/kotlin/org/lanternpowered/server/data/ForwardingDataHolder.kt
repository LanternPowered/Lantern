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

import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import java.util.Optional
import java.util.OptionalDouble
import java.util.OptionalInt
import java.util.OptionalLong

interface ForwardingDataHolder : DataHolder, ValueContainerBase {

    /**
     * The delegate [DataHolder].
     */
    val delegateDataHolder: DataHolder

    @JvmDefault
    override fun <E : Any> get(key: Key<out Value<E>>): Optional<E> = this.delegateDataHolder.get(key)

    @JvmDefault
    override fun getInt(key: Key<out Value<Int>>): OptionalInt = this.delegateDataHolder.getInt(key)

    @JvmDefault
    override fun getDouble(key: Key<out Value<Double>>): OptionalDouble = this.delegateDataHolder.getDouble(key)

    @JvmDefault
    override fun getLong(key: Key<out Value<Long>>): OptionalLong = this.delegateDataHolder.getLong(key)

    @JvmDefault
    override fun <E : Any, V : Value<E>> getValue(key: Key<V>): Optional<V> = this.delegateDataHolder.getValue(key)

    @JvmDefault
    override fun <E : Any> require(key: Key<out Value<E>>): E = this.delegateDataHolder.require(key)

    @JvmDefault
    override fun <E : Any> getOrElse(key: Key<out Value<E>>, defaultValue: E): E = this.delegateDataHolder.getOrElse(key, defaultValue)

    @JvmDefault
    override fun <E : Any> getOrNull(key: Key<out Value<E>>): E? = this.delegateDataHolder.getOrNull(key)

    @JvmDefault
    override fun supports(key: Key<*>): Boolean = this.delegateDataHolder.supports(key)

    @JvmDefault
    override fun supports(value: Value<*>): Boolean = this.delegateDataHolder.supports(value)

    @JvmDefault
    override fun getKeys(): Set<Key<*>> = this.delegateDataHolder.keys

    @JvmDefault
    override fun getValues(): Set<Value.Immutable<*>> = this.delegateDataHolder.values
}
