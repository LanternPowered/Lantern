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
package org.lanternpowered.server.state.property

import org.lanternpowered.api.util.collections.toImmutableSet
import org.lanternpowered.server.state.StateKeyValueTransformer
import org.lanternpowered.server.state.identityStateKeyValueTransformer
import org.lanternpowered.api.namespace.NamespacedKey
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.state.BooleanStateProperty
import org.spongepowered.api.state.EnumStateProperty
import org.spongepowered.api.state.IntegerStateProperty
import java.util.function.Supplier
import kotlin.reflect.KClass

/**
 * Creates a [BooleanStateProperty].
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @return The constructed boolean state property
 */
fun booleanStatePropertyOf(key: NamespacedKey, valueKey: Supplier<out Key<out Value<Boolean>>>): BooleanStateProperty =
        booleanStatePropertyOf(key, valueKey.get())

/**
 * Creates a [BooleanStateProperty].
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @return The constructed boolean state property
 */
fun booleanStatePropertyOf(key: NamespacedKey, valueKey: Key<out Value<Boolean>>): BooleanStateProperty =
        LanternBooleanStateProperty(key, valueKey, identityStateKeyValueTransformer())

/**
 * Creates a [BooleanStateProperty] with a [StateKeyValueTransformer].
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @param keyValueTransformer The transformer to translate between state and key values
 * @return The constructed boolean state property
 */
fun <T> booleanStatePropertyOf(key: NamespacedKey, valueKey: Key<out Value<T>>,
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
fun <E : Enum<E>> enumStatePropertyOf(key: NamespacedKey, valueKey: Key<out Value<E>>, values: Iterable<E>): EnumStateProperty<E>
        = LanternEnumStateProperty(key, values.iterator().next().javaClass, values.toImmutableSet(), valueKey)

/**
 * Creates a [EnumStateProperty] with the given values.
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @param values The values that are available for the state property
 * @return The constructed enum state property
 */
fun <E : Enum<E>> enumStatePropertyOf(key: NamespacedKey, valueKey: Supplier<out Key<out Value<E>>>, vararg values: E): EnumStateProperty<E>
        = enumStatePropertyOf(key, valueKey.get(), values.toImmutableSet())

/**
 * Creates a [EnumStateProperty] with the given values.
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @param values The values that are available for the state property
 * @return The constructed enum state property
 */
fun <E : Enum<E>> enumStatePropertyOf(key: NamespacedKey, valueKey: Key<out Value<E>>, vararg values: E): EnumStateProperty<E>
        = enumStatePropertyOf(key, valueKey, values.toImmutableSet())

/**
 * Creates a [EnumStateProperty] with all the values of the given enum [Class].
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @param type The enum class
 * @return The constructed enum state property
 */
fun <E : Enum<E>> enumStatePropertyOf(key: NamespacedKey, valueKey: Key<out Value<E>>, type: Class<E>): EnumStateProperty<E>
        = LanternEnumStateProperty(key, type, type.enumConstants.toImmutableSet(), valueKey)

/**
 * Creates a [EnumStateProperty] with all the values of the given enum [KClass].
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @param type The enum class
 * @return The constructed enum state property
 */
fun <E : Enum<E>> enumStatePropertyOf(key: NamespacedKey, valueKey: Key<out Value<E>>, type: KClass<E>): EnumStateProperty<E>
        = enumStatePropertyOf(key, valueKey, type.java)

/**
 * Creates a [EnumStateProperty] with all the values of the given enum type [E].
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @param E The enum type
 * @return The constructed enum state property
 */
inline fun <reified E : Enum<E>> enumStatePropertyOf(key: NamespacedKey, valueKey: Key<out Value<E>>): EnumStateProperty<E>
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
fun intStatePropertyOf(key: NamespacedKey, valueKey: Key<out Value<Int>>, values: Iterable<Int>): IntegerStateProperty
        = LanternIntStateProperty(key, values.toImmutableSet(), valueKey, identityStateKeyValueTransformer())

/**
 * Creates a [IntegerStateProperty] with all the values from the given array.
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @param values The values that are available for the state property
 * @return The constructed int state property
 */
fun intStatePropertyOf(key: NamespacedKey, valueKey: Supplier<out Key<out Value<Int>>>, vararg values: Int): IntegerStateProperty
        = intStatePropertyOf(key, valueKey.get(), *values)

/**
 * Creates a [IntegerStateProperty] with all the values from the given array.
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @param values The values that are available for the state property
 * @return The constructed int state property
 */
fun intStatePropertyOf(key: NamespacedKey, valueKey: Key<out Value<Int>>, vararg values: Int): IntegerStateProperty
        = LanternIntStateProperty(key, values.toImmutableSet(), valueKey, identityStateKeyValueTransformer())

/**
 * Creates a [IntegerStateProperty] with all the values within the given [IntRange].
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @param valueRange The range of values that are available for the state property
 * @return The constructed int state property
 */
fun intStatePropertyOf(key: NamespacedKey, valueKey: Supplier<out Key<out Value<Int>>>, valueRange: IntRange): IntegerStateProperty =
        intStatePropertyOf(key, valueKey.get(), valueRange)

/**
 * Creates a [IntegerStateProperty] with all the values within the given [IntRange].
 *
 * @param key The catalog key of the state property
 * @param valueKey The value key this property is mapped to
 * @param valueRange The range of values that are available for the state property
 * @return The constructed int state property
 */
fun intStatePropertyOf(key: NamespacedKey, valueKey: Key<out Value<Int>>, valueRange: IntRange): IntegerStateProperty
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
fun <T> intStatePropertyOf(key: NamespacedKey, valueKey: Key<out Value<T>>, values: Iterable<Int>,
                           keyValueTransformer: StateKeyValueTransformer<Int, T>): IntegerStateProperty
        = LanternIntStateProperty(key, values.toImmutableSet(), valueKey, keyValueTransformer)

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
fun <T> intStatePropertyOf(key: NamespacedKey, valueKey: Supplier<out Key<out Value<T>>>, values: Iterable<Int>,
                           keyValueTransformer: StateKeyValueTransformer<Int, T>): IntegerStateProperty
        = intStatePropertyOf(key, valueKey.get(), values, keyValueTransformer)

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
fun <T> intStatePropertyOf(key: NamespacedKey, valueKey: Key<out Value<T>>, vararg values: Int,
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
fun <T> intStatePropertyOf(key: NamespacedKey, valueKey: Key<out Value<T>>, valueRange: IntRange,
                           keyValueTransformer: StateKeyValueTransformer<Int, T>): IntegerStateProperty
        = LanternIntStateProperty(key, valueRange.toImmutableSet(), valueKey, keyValueTransformer)
