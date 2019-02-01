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
package org.lanternpowered.server.state

import com.google.common.collect.ImmutableSet
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.data.key.Key
import org.spongepowered.api.data.value.BaseValue
import org.spongepowered.api.data.value.mutable.Value
import org.spongepowered.api.state.EnumStateProperty
import java.util.Arrays
import java.util.Optional
import java.util.function.Predicate

class LanternEnumStateProperty<E : Enum<E>> private constructor(
        key: CatalogKey, valueClass: Class<E>, possibleValues: ImmutableSet<E>, valueKey: Key<out BaseValue<E>>
) : AbstractStateProperty<E, E>(key, valueClass, possibleValues, valueKey), EnumStateProperty<E> {

    override fun parseValue(value: String): Optional<E> {
        for (enumValue in valueClass.enumConstants) {
            if (enumValue.name.equals(value, ignoreCase = true)) {
                return Optional.of(enumValue)
            }
        }
        return Optional.empty()
    }

    @Suppress("UNCHECKED_CAST")
    companion object {

        /**
         * Creates a new enum trait with the specified name and the possible values.
         *
         * The possible values array may not be empty.
         *
         * @param key the key
         * @param valueKey the value key that should be attached to the trait
         * @param possibleValues the possible values
         * @return the enum trait
         */
        @JvmStatic
        fun <E : Enum<E>> of(key: CatalogKey, valueKey: Key<out BaseValue<E>>, possibleValues: Iterable<E>): EnumStateProperty<E> {
            check(possibleValues.iterator().hasNext()) { "possibleValues may not be empty" }
            return LanternEnumStateProperty(key, possibleValues.iterator().javaClass as Class<E>, ImmutableSet.copyOf(possibleValues), valueKey)
        }

        @JvmStatic
        fun <E : Enum<E>> minecraft(id: String, valueKey: Key<out BaseValue<E>>, possibleValues: Iterable<E>): EnumStateProperty<E> {
            return of(CatalogKey.minecraft(id), valueKey, possibleValues)
        }

        /**
         * Creates a new enum trait with the specified name and all the values from the enum.
         *
         * The enum must contain values.
         *
         * @param key the key
         * @param valueKey the value key that should be attached to the trait
         * @param enumClass the enum class
         * @return the enum trait
         */
        @JvmStatic
        fun <E : Enum<E>> of(key: CatalogKey, valueKey: Key<out BaseValue<E>>, enumClass: Class<E>): EnumStateProperty<E> {
            check(enumClass.enumConstants.isNotEmpty()) { "enumClass must contain values" }
            return LanternEnumStateProperty(key, enumClass, ImmutableSet.copyOf(enumClass.enumConstants), valueKey)
        }

        @JvmStatic
        fun <E : Enum<E>> minecraft(id: String, valueKey: Key<out Value<E>>, enumClass: Class<E>): EnumStateProperty<E> {
            return of(CatalogKey.minecraft(id), valueKey, enumClass)
        }

        /**
         * Creates a new enum trait with the specified name and all the values
         * from the enum that match the [Predicate].
         *
         *
         * The enum must contain values.
         *
         * @param key the key
         * @param valueKey the value key that should be attached to the trait
         * @param enumClass the enum class
         * @return the enum trait
         */
        @JvmStatic
        fun <E : Enum<E>> of(key: CatalogKey, valueKey: Key<out BaseValue<E>>,
                             enumClass: Class<E>, predicate: Predicate<E>): EnumStateProperty<E> {
            check(enumClass.enumConstants.isNotEmpty()) { "enumClass must contain values" }
            return LanternEnumStateProperty(key, enumClass, Arrays.stream(enumClass.enumConstants)
                    .filter(predicate).collect(ImmutableSet.toImmutableSet()), valueKey)
        }

        @JvmStatic
        fun <E : Enum<E>> minecraft(id: String, valueKey: Key<out Value<E>>,
                                    enumClass: Class<E>, predicate: Predicate<E>): EnumStateProperty<E> {
            return of(CatalogKey.minecraft(id), valueKey, enumClass, predicate)
        }

        /**
         * Creates a new enum trait with the specified name and all the values
         * from the enum that match the [Predicate].
         *
         *
         * The enum must contain values.
         *
         * @param key the key
         * @param valueKey the value key that should be attached to the trait
         * @param value the value
         * @param values the values
         * @return the enum trait
         */
        @JvmStatic
        fun <E : Enum<E>> of(key: CatalogKey, valueKey: Key<out Value<E>>, value: E, vararg values: E): EnumStateProperty<E> {
            check(values.isNotEmpty()) { "enumClass must contain values" }
            return LanternEnumStateProperty(key, value.javaClass,
                    ImmutableSet.builder<E>().add(value).addAll(values.asList()).build(), valueKey)
        }

        @JvmStatic
        fun <E : Enum<E>> minecraft(id: String, valueKey: Key<out Value<E>>, value: E, vararg values: E): EnumStateProperty<E> {
            return of(CatalogKey.minecraft(id), valueKey, value, *values)
        }
    }
}
