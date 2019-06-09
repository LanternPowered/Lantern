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

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.server.data.key.BoundedValueKey
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.BoundedValue
import org.spongepowered.api.data.value.ListValue
import org.spongepowered.api.data.value.MapValue
import org.spongepowered.api.data.value.OptionalValue
import org.spongepowered.api.data.value.SetValue
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.data.value.ValueContainer
import org.spongepowered.api.data.value.WeightedCollectionValue
import org.spongepowered.api.util.weighted.WeightedTable
import java.util.Optional
import kotlin.reflect.KClass

@Suppress("UNCHECKED_CAST")
object ValueFactory : Value.Factory {

    private val mutableFactories = mutableMapOf<Class<*>, (Key<*>, Any) -> Value<*>>()
    private val immutableFactories = mutableMapOf<Class<*>, (Key<*>, Any) -> Value<*>>()

    /**
     * Registers a factory for the mutable variant of the given value type.
     */
    private inline fun <reified V : Value<E>, E : Any> registerMutable(noinline factory: (Key<V>, E) -> V) {
        this.mutableFactories[V::class.java] = factory.uncheckedCast()
    }

    /**
     * Registers a factory for the immutable variant of the given value type.
     */
    private inline fun <reified V : Value<E>, E : Any> registerImmutable(noinline factory: (Key<V>, E) -> V) {
        this.immutableFactories[V::class.java] = factory.uncheckedCast()
    }

    init {
        registerMutable<Value<Any>, Any> { key, value -> LanternMutableValue(key, value) }
        registerImmutable<Value<Any>, Any> { key, value -> LanternImmutableValue(key, value) }

        registerMutable<BoundedValue<Any>, Any> { key, value ->
            key as BoundedValueKey<*, *>
            LanternMutableBoundedValue(key, value, key.minimum(), key.maximum(), key.comparator.uncheckedCast())
        }
        registerImmutable<BoundedValue<Any>, Any> { key, value ->
            key as BoundedValueKey<*, *>
            LanternImmutableBoundedValue(key, value, key.minimum(), key.maximum(), key.comparator.uncheckedCast())
        }

        registerMutable<ListValue<Any>, MutableList<Any>> { key, value -> LanternMutableListValue(key, value) }
        registerImmutable<ListValue<Any>, MutableList<Any>> { key, value -> LanternImmutableListValue(key, value) }

        registerMutable<MapValue<Any, Any>, MutableMap<Any, Any>> { key, value -> LanternMutableMapValue(key, value) }
        registerImmutable<MapValue<Any, Any>, MutableMap<Any, Any>> { key, value -> LanternImmutableMapValue(key, value) }

        registerMutable<SetValue<Any>, MutableSet<Any>> { key, value -> LanternMutableSetValue(key, value) }
        registerImmutable<SetValue<Any>, MutableSet<Any>> { key, value -> LanternImmutableSetValue(key, value) }

        registerMutable<OptionalValue<Any>, Optional<Any>> { key, value -> LanternMutableOptionalValue(key, value) }
        registerImmutable<OptionalValue<Any>, Optional<Any>> { key, value -> LanternImmutableOptionalValue(key, value) }

        registerMutable<WeightedCollectionValue<Any>, WeightedTable<Any>> { key, value -> LanternMutableWeightedCollectionValue(key, value) }
        registerImmutable<WeightedCollectionValue<Any>, WeightedTable<Any>> { key, value -> LanternImmutableWeightedCollectionValue(key, value) }
    }

    override fun <V : Value<E>, E : Any> mutableOf(key: Key<V>, element: E): V = mutableFactoryOf(key.valueToken)(key, element)

    override fun <V : Value<E>, E : Any> immutableOf(key: Key<V>, element: E): V = immutableFactoryOf(key.valueToken)(key, element)

    /**
     * Gets a mutable [Value] supplier for the given type.
     */
    inline fun <reified V : Value<E>, E : Any> mutableFactoryOf() = mutableFactoryOf(V::class.java)

    /**
     * Gets a mutable [Value] supplier for the given type.
     */
    fun <V : Value<E>, E : Any> mutableFactoryOf(valueType: TypeToken<V>) = mutableFactoryOf(valueType.rawType.uncheckedCast<Class<V>>())

    /**
     * Gets a mutable [Value] supplier for the given type.
     */
    fun <V : Value<E>, E : Any> mutableFactoryOf(valueType: KClass<V>) = mutableFactoryOf(valueType.java)

    /**
     * Gets a mutable [Value] supplier for the given type.
     */
    fun <V : Value<E>, E : Any> mutableFactoryOf(valueType: Class<V>) =
            this.mutableFactories[valueType].uncheckedCast<(Key<V>, E) -> V>()

    /**
     * Gets a immutable [Value] supplier for the given type.
     */
    inline fun <reified V : Value<E>, E : Any> immutableFactoryOf() = immutableFactoryOf(V::class.java)

    /**
     * Gets a immutable [Value] supplier for the given type.
     */
    fun <V : Value<E>, E : Any> immutableFactoryOf(valueType: KClass<V>) = immutableFactoryOf(valueType.java)

    /**
     * Gets a immutable [Value] supplier for the given type.
     */
    fun <V : Value<E>, E : Any> immutableFactoryOf(valueType: TypeToken<V>) = immutableFactoryOf(valueType.rawType.uncheckedCast<Class<V>>())

    /**
     * Gets a immutable [Value] supplier for the given type.
     */
    fun <V : Value<E>, E : Any> immutableFactoryOf(valueType: Class<V>) =
            this.immutableFactories[valueType].uncheckedCast<(Key<V>, E) -> V>()

    /**
     * Converts the [Value]s of the [ValueContainer] into a nicely
     * formatted `String`.
     *
     * @param valueContainer The value container
     * @return The string
     */
    fun toString(valueContainer: ValueContainer) = toString(valueContainer.values)

    /**
     * Converts the [Value]s into a nicely
     * formatted `String`.
     *
     * @param values The values
     * @return The string
     */
    fun toString(values: Iterable<Value<*>>) = ToStringHelper(brackets = ToStringHelper.Brackets.SQUARE)
            .apply {
                values.forEach { value ->
                    add(value.key.key.toString(), value.get())
                }
            }
            .toString()
}
