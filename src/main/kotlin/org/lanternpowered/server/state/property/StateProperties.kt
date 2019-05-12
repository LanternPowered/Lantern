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
package org.lanternpowered.server.state.property

import org.lanternpowered.api.ext.*
import org.lanternpowered.server.state.StateKeyValueTransformer
import org.lanternpowered.server.state.identityStateKeyValueTransformer
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.data.key.Key
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.state.BooleanStateProperty
import org.spongepowered.api.state.EnumStateProperty
import org.spongepowered.api.state.IntegerStateProperty
import kotlin.reflect.KClass

/**
 * Creates a [BooleanStateProperty].
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @return The constructed boolean state property
 */
fun booleanStatePropertyOf(key: CatalogKey, valueKey: Key<out Value<Boolean>>): BooleanStateProperty
        = LanternBooleanStateProperty(key, valueKey, identityStateKeyValueTransformer())

/**
 * Creates a [BooleanStateProperty] with a [StateKeyValueTransformer].
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @param keyValueTransformer The transformer to translate between state and key values
 * @return The constructed boolean state property
 */
fun <T> booleanStatePropertyOf(key: CatalogKey, valueKey: Key<out Value<T>>,
                               keyValueTransformer: StateKeyValueTransformer<Boolean, T>): BooleanStateProperty
        = LanternBooleanStateProperty(key, valueKey, keyValueTransformer)

// Enum state properties

/**
 * Creates a [EnumStateProperty] with the given values.
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @param values The values that are available for the state property
 * @return The constructed enum state property
 */
fun <E : Enum<E>> enumStatePropertyOf(key: CatalogKey, valueKey: Key<out Value<E>>, values: Iterable<E>): EnumStateProperty<E>
        = LanternEnumStateProperty(key, values.iterator().next().javaClass, values.toImmutableSet(), valueKey)

/**
 * Creates a [EnumStateProperty] with the given values.
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @param values The values that are available for the state property
 * @return The constructed enum state property
 */
fun <E : Enum<E>> enumStatePropertyOf(key: CatalogKey, valueKey: Key<out Value<E>>, vararg values: E): EnumStateProperty<E>
        = enumStatePropertyOf(key, valueKey, values.toImmutableSet())

/**
 * Creates a [EnumStateProperty] with all the values of the given enum [Class].
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @param type The enum class
 * @return The constructed enum state property
 */
fun <E : Enum<E>> enumStatePropertyOf(key: CatalogKey, valueKey: Key<out Value<E>>, type: Class<E>): EnumStateProperty<E>
        = LanternEnumStateProperty(key, type, type.enumConstants.toImmutableSet(), valueKey)

/**
 * Creates a [EnumStateProperty] with all the values of the given enum [KClass].
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @param type The enum class
 * @return The constructed enum state property
 */
fun <E : Enum<E>> enumStatePropertyOf(key: CatalogKey, valueKey: Key<out Value<E>>, type: KClass<E>): EnumStateProperty<E>
        = enumStatePropertyOf(key, valueKey, type.java)

/**
 * Creates a [EnumStateProperty] with all the values of the given enum type [E].
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @param E The enum type
 * @return The constructed enum state property
 */
inline fun <reified E : Enum<E>> enumStatePropertyOf(key: CatalogKey, valueKey: Key<out Value<E>>): EnumStateProperty<E>
        = enumStatePropertyOf(key, valueKey, E::class)

// Int state properties

/**
 * Creates a [IntegerStateProperty] with all the values from the given collection.
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @param values The values that are available for the state property
 * @return The constructed int state property
 */
fun intStatePropertyOf(key: CatalogKey, valueKey: Key<out Value<Int>>, values: Iterable<Int>): IntegerStateProperty
        = LanternIntStateProperty(key, values.toImmutableSet(), valueKey, identityStateKeyValueTransformer())

/**
 * Creates a [IntegerStateProperty] with all the values from the given array.
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @param values The values that are available for the state property
 * @return The constructed int state property
 */
fun intStatePropertyOf(key: CatalogKey, valueKey: Key<out Value<Int>>, vararg values: Int): IntegerStateProperty
        = LanternIntStateProperty(key, values.toImmutableSet(), valueKey, identityStateKeyValueTransformer())

/**
 * Creates a [IntegerStateProperty] with all the values within the given [IntRange].
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @param valueRange The range of values that are available for the state property
 * @return The constructed int state property
 */
fun intStatePropertyOf(key: CatalogKey, valueKey: Key<out Value<Int>>, valueRange: IntRange): IntegerStateProperty
        = LanternIntStateProperty(key, valueRange.toImmutableSet(), valueKey, identityStateKeyValueTransformer())

/**
 * Creates a [IntegerStateProperty] with all the values from the given collection. An
 * additional [StateKeyValueTransformer] can be used to translate between int and key
 * based values of type [T].
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @param values The values that are available for the state property
 * @return The constructed int state property
 */
fun <T> intStatePropertyOf(key: CatalogKey, valueKey: Key<out Value<T>>, values: Iterable<Int>,
                           keyValueTransformer: StateKeyValueTransformer<Int, T>): IntegerStateProperty
        = LanternIntStateProperty(key, values.toImmutableSet(), valueKey, keyValueTransformer)

/**
 * Creates a [IntegerStateProperty] with all the values from the given array. An
 * additional [StateKeyValueTransformer] can be used to translate between int and key
 * based values of type [T].
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @param values The values that are available for the state property
 * @return The constructed int state property
 */
fun <T> intStatePropertyOf(key: CatalogKey, valueKey: Key<out Value<T>>, vararg values: Int,
                           keyValueTransformer: StateKeyValueTransformer<Int, T>): IntegerStateProperty
        = LanternIntStateProperty(key, values.toImmutableSet(), valueKey, keyValueTransformer)

/**
 * Creates a [IntegerStateProperty] with all the values within the given [IntRange]. An
 * additional [StateKeyValueTransformer] can be used to translate between int and key
 * based values of type [T].
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @param valueRange The range of values that are available for the state property
 * @return The constructed int state property
 */
fun <T> intStatePropertyOf(key: CatalogKey, valueKey: Key<out Value<T>>, valueRange: IntRange,
                           keyValueTransformer: StateKeyValueTransformer<Int, T>): IntegerStateProperty
        = LanternIntStateProperty(key, valueRange.toImmutableSet(), valueKey, keyValueTransformer)
