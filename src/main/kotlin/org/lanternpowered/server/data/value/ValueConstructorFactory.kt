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

import org.lanternpowered.api.ext.uncheckedCast
import org.lanternpowered.server.data.key.BoundedValueKey
import org.lanternpowered.server.data.key.ValueKey
import org.spongepowered.api.data.value.BoundedValue
import org.spongepowered.api.data.value.ListValue
import org.spongepowered.api.data.value.MapValue
import org.spongepowered.api.data.value.SetValue
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.data.value.WeightedCollectionValue
import org.spongepowered.api.util.weighted.WeightedTable

@Suppress("UNCHECKED_CAST")
object ValueConstructorFactory {

    fun <V : Value<E>, E : Any> getConstructor(key: ValueKey<V, E>): ValueConstructor<V, E> {
        if (key is BoundedValueKey<*,*>) {
            return BoundedValueConstructor(key as BoundedValueKey<BoundedValue<E>, E>) as ValueConstructor<V, E>
        }
        val valueType = key.valueToken.rawType
        var valueConstructor: ValueConstructor<V, E>
        if (ListValue::class.java.isAssignableFrom(valueType)) {
            valueConstructor = SimpleValueConstructor(key,
                    { key1, value -> LanternMutableListValue(key1.uncheckedCast(), value as MutableList<*>) as V },
                    { key1, value -> LanternImmutableListValue(key1.uncheckedCast(), value as MutableList<*>) as V })
        } else if (SetValue::class.java.isAssignableFrom(valueType)) {
            valueConstructor = SimpleValueConstructor(key,
                    { key1, value -> LanternMutableSetValue(key1.uncheckedCast(), value as MutableSet<*>) as V },
                    { key1, value -> LanternImmutableSetValue(key1.uncheckedCast(), value as MutableSet<*>) as V })
        } else if (MapValue::class.java.isAssignableFrom(valueType)) {
            valueConstructor = SimpleValueConstructor(key,
                    { key1, value -> LanternMutableMapValue(key1.uncheckedCast(), value as MutableMap<*,*>) as V },
                    { key1, value -> LanternImmutableMapValue(key1.uncheckedCast(), value as MutableMap<*,*>) as V })
        } else if (WeightedCollectionValue::class.java.isAssignableFrom(valueType)) {
            valueConstructor = SimpleValueConstructor(key,
                    { key1, value -> LanternMutableWeightedCollectionValue(key1.uncheckedCast(), value as WeightedTable<*>) as V },
                    { key1, value -> LanternImmutableWeightedCollectionValue(key1.uncheckedCast(), value as WeightedTable<*>) as V })
        } else {
            valueConstructor = SimpleValueConstructor(key,
                    { key1, value -> LanternMutableValue(key1.uncheckedCast(), value) as V },
                    { key1, value -> LanternImmutableValue(key1.uncheckedCast(), value) as V })

            val elementType = key.elementToken.rawType
            if (Enum::class.java.isAssignableFrom(elementType)) {
                valueConstructor = CachedEnumValueConstructor(valueConstructor.uncheckedCast(), elementType.uncheckedCast())
            } else if (elementType == Boolean::class.java) {
                valueConstructor = CachedBooleanValueConstructor(valueConstructor.uncheckedCast()).uncheckedCast()
            }
        }
        return valueConstructor
    }
}