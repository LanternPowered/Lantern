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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.ext

import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.BoundedValue
import org.spongepowered.api.data.value.ListValue
import org.spongepowered.api.data.value.MapValue
import org.spongepowered.api.data.value.OptionalValue
import org.spongepowered.api.data.value.SetValue
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.data.value.WeightedCollectionValue
import org.spongepowered.api.util.weighted.WeightedTable
import java.util.Optional

inline fun <E> immutableValueOf(key: Key<out Value<E>>, element: E): Value.Immutable<E>
        = Value.immutableOf(key, element).asImmutable()

inline fun <E> immutableValueOf(key: Key<out BoundedValue<E>>, element: E): BoundedValue.Immutable<E>
        = Value.immutableOf(key, element).asImmutable()

inline fun <E> immutableValueOf(key: Key<out ListValue<E>>, element: List<E>): ListValue.Immutable<E>
        = Value.immutableOf(key, element).asImmutable()

inline fun <E> immutableValueOf(key: Key<out SetValue<E>>, element: Set<E>): SetValue.Immutable<E>
        = Value.immutableOf(key, element).asImmutable()

inline fun <K, V> immutableValueOf(key: Key<out MapValue<K, V>>, element: Map<K, V>): MapValue.Immutable<K, V>
        = Value.immutableOf(key, element).asImmutable()

inline fun <E> immutableValueOf(key: Key<out WeightedCollectionValue<E>>, element: WeightedTable<E>): WeightedCollectionValue.Immutable<E>
        = Value.immutableOf(key, element).asImmutable()

inline fun <E> immutableValueOf(key: Key<out OptionalValue<E>>, element: Optional<E>): OptionalValue.Immutable<E>
        = Value.immutableOf(key, element).asImmutable()

inline fun <E> mutableValueOf(key: Key<out Value<E>>, element: E): Value.Mutable<E>
        = Value.mutableOf(key, element).asMutable()

inline fun <E> mutableValueOf(key: Key<out BoundedValue<E>>, element: E): BoundedValue.Mutable<E>
        = Value.mutableOf(key, element).asMutable()

inline fun <E> mutableValueOf(key: Key<out ListValue<E>>, element: List<E>): ListValue.Mutable<E>
        = Value.mutableOf(key, element).asMutable()

inline fun <E> mutableValueOf(key: Key<out SetValue<E>>, element: Set<E>): SetValue.Mutable<E>
        = Value.mutableOf(key, element).asMutable()

inline fun <K, V> mutableValueOf(key: Key<out MapValue<K, V>>, element: Map<K, V>): MapValue.Mutable<K, V>
        = Value.mutableOf(key, element).asMutable()

inline fun <E> mutableValueOf(key: Key<out WeightedCollectionValue<E>>, element: WeightedTable<E>): WeightedCollectionValue.Mutable<E>
        = Value.mutableOf(key, element).asMutable()

inline fun <E> mutableValueOf(key: Key<out OptionalValue<E>>, element: Optional<E>): OptionalValue.Mutable<E>
        = Value.mutableOf(key, element).asMutable()
