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
import org.spongepowered.api.state.IntegerStateProperty
import java.util.Optional

class LanternIntStateProperty<V> private constructor(
        key: CatalogKey, possibleValues: ImmutableSet<Int>, valueKey: Key<out BaseValue<V>>, keyValueTransformer: StateKeyValueTransformer<Int, V>
) : AbstractStateProperty<Int, V>(key, Int::class.java, possibleValues, valueKey, keyValueTransformer), IntegerStateProperty {

    override fun parseValue(value: String): Optional<Int> {
        return try {
            Optional.of(Integer.parseInt(value))
        } catch (e: Exception) {
            Optional.empty()
        }
    }

    companion object {

        /**
         * Creates a new integer trait with the specified name and the possible values.
         *
         *
         * The possible values array may not be empty.
         *
         * @param key The key
         * @param valueKey The value key that should be attached to the trait
         * @param possibleValues the possible values
         * @return the integer trait
         */
        @JvmStatic
        fun of(key: CatalogKey, valueKey: Key<out Value<Int>>, vararg possibleValues: Int): LanternIntStateProperty<Int> {
            return ofTransformed(key, valueKey, StateKeyValueTransformer.identity(), possibleValues.asList())
        }

        @JvmStatic
        fun minecraft(id: String, valueKey: Key<out Value<Int>>, vararg possibleValues: Int): LanternIntStateProperty<Int> {
            return of(CatalogKey.minecraft(id), valueKey, possibleValues.asList())
        }

        /**
         * Creates a new integer trait with the specified name and the possible values.
         *
         *
         * The possible values array may not be empty.
         *
         * @param key The key
         * @param valueKey The value key that should be attached to the trait
         * @param keyValueTransformer The key trait value transformer
         * @param possibleValues the possible values
         * @return the integer trait
         */
        @JvmStatic
        fun <V> ofTransformed(key: CatalogKey, valueKey: Key<out Value<V>>,
                              keyValueTransformer: StateKeyValueTransformer<Int, V>, vararg possibleValues: Int): LanternIntStateProperty<V> {
            check(possibleValues.isNotEmpty()) { "possibleValues may not be empty" }
            val builder = ImmutableSet.builder<Int>()
            for (possibleValue in possibleValues) {
                builder.add(possibleValue)
            }
            return LanternIntStateProperty(key, builder.build(), valueKey, keyValueTransformer)
        }

        @JvmStatic
        fun <V> minecraftTransformed(id: String, valueKey: Key<out Value<V>>,
                                     keyTraitValueTransformer: StateKeyValueTransformer<Int, V>, vararg possibleValues: Int): LanternIntStateProperty<V> {
            return ofTransformed(CatalogKey.minecraft(id), valueKey, keyTraitValueTransformer, *possibleValues)
        }

        /**
         * Creates a new integer trait with the specified name and the possible values.
         *
         *
         * The possible values array may not be empty.
         *
         * @param key The key
         * @param valueKey The value key that should be attached to the trait
         * @param possibleValues the possible values
         * @return the integer trait
         */
        @JvmStatic
        fun <V> of(key: CatalogKey, valueKey: Key<out Value<V>>, possibleValues: Iterable<Int>): LanternIntStateProperty<V> {
            return ofTransformed(key, valueKey, StateKeyValueTransformer.identity(), possibleValues)
        }

        @JvmStatic
        fun <V> minecraft(id: String, valueKey: Key<out Value<V>>, possibleValues: Iterable<Int>): LanternIntStateProperty<V> {
            return of(CatalogKey.minecraft(id), valueKey, possibleValues)
        }

        /**
         * Creates a new integer trait with the specified name and the possible values.
         *
         *
         * The possible values array may not be empty.
         *
         * @param key The key
         * @param valueKey The value key that should be attached to the trait
         * @param keyValueTransformer The key trait value transformer
         * @param possibleValues the possible values
         * @return the integer trait
         */
        @JvmStatic
        fun <V> ofTransformed(key: CatalogKey,
                              valueKey: Key<out Value<V>>,
                              keyValueTransformer: StateKeyValueTransformer<Int, V>,
                              possibleValues: Iterable<Int>): LanternIntStateProperty<V> {
            check(possibleValues.iterator().hasNext()) { "possibleValues may not be empty" }
            return LanternIntStateProperty(key, ImmutableSet.copyOf(possibleValues), valueKey, keyValueTransformer)
        }

        @JvmStatic
        fun <V> minecraftTransformed(id: String,
                                     valueKey: Key<out Value<V>>,
                                     keyValueTransformer: StateKeyValueTransformer<Int, V>,
                                     possibleValues: Iterable<Int>): LanternIntStateProperty<V> {
            return ofTransformed(CatalogKey.minecraft(id), valueKey, keyValueTransformer, possibleValues)
        }

        /**
         * Creates a new integer trait with the specified name and the values between
         * the minimum (inclusive) and the maximum (exclusive) value.
         *
         *
         * The difference between the minimum and the maximum value must
         * be greater then zero.
         *
         * @param key The key
         * @param valueKey The value key that should be attached to the trait
         * @param min The minimum value
         * @param max The maximum value
         * @return The integer trait
         */
        @JvmStatic
        fun ofRange(key: CatalogKey, valueKey: Key<out Value<Int>>, min: Int, max: Int): LanternIntStateProperty<Int> {
            return ofRangeTransformed(key, valueKey, StateKeyValueTransformer.identity(), min, max)
        }

        @JvmStatic
        fun minecraftRange(id: String, valueKey: Key<out Value<Int>>, min: Int, max: Int): LanternIntStateProperty<Int> {
            return ofRangeTransformed(CatalogKey.minecraft(id), valueKey, StateKeyValueTransformer.identity(), min, max)
        }

        /**
         * Creates a new integer trait with the specified name and the values between
         * the minimum (inclusive) and the maximum (exclusive) value.
         *
         *
         * The difference between the minimum and the maximum value must
         * be greater then zero.
         *
         * @param key The name
         * @param valueKey The value key that should be attached to the trait
         * @param keyValueTransformer The key trait value transformer
         * @param min The minimum value
         * @param max The maximum value
         * @return The integer trait
         */
        @JvmStatic
        fun <V> ofRangeTransformed(key: CatalogKey, valueKey: Key<out Value<V>>,
                                   keyValueTransformer: StateKeyValueTransformer<Int, V>, min: Int, max: Int): LanternIntStateProperty<V> {
            check(max - min > 0) { "difference between min and max must be greater then zero" }
            val set = ImmutableSet.builder<Int>()
            for (i in min..max) {
                set.add(i)
            }
            return LanternIntStateProperty(key, set.build(), valueKey, keyValueTransformer)
        }

        @JvmStatic
        fun <V> minecraftRangeTransformed(id: String, valueKey: Key<out Value<V>>,
                                          keyValueTransformer: StateKeyValueTransformer<Int, V>, min: Int, max: Int): LanternIntStateProperty<V> {
            return ofRangeTransformed(CatalogKey.minecraft(id), valueKey, keyValueTransformer, min, max)
        }
    }
}
