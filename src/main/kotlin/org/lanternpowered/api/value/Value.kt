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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.value

import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.BoundedValue
import org.spongepowered.api.data.value.ListValue
import org.spongepowered.api.data.value.MapValue
import org.spongepowered.api.data.value.SetValue
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.data.value.WeightedCollectionValue
import org.spongepowered.api.util.weighted.WeightedTable
import java.util.function.Supplier

inline fun <E> immutableValueOf(key: Key<out Value<E>>, element: E): Value.Immutable<E>
        = Value.immutableOf(key, element)

inline fun <E> immutableValueOf(key: Supplier<out Key<out Value<E>>>, element: E): Value.Immutable<E>
        = Value.immutableOf(key, element)

inline fun <E> immutableValueOf(key: Key<out BoundedValue<E>>, element: E): BoundedValue.Immutable<E>
        = BoundedValue.immutableOf(key, element)

inline fun <E> immutableValueOf(key: Supplier<out Key<out BoundedValue<E>>>, element: E): BoundedValue.Immutable<E>
        = BoundedValue.immutableOf(key, element)

inline fun <E> immutableValueOf(key: Key<out ListValue<E>>, element: List<E>): ListValue.Immutable<E>
        = Value.immutableOf(key, element)

inline fun <E> immutableValueOf(key: Supplier<out Key<out ListValue<E>>>, element: List<E>): ListValue.Immutable<E>
        = Value.immutableOf(key, element)

inline fun <E> immutableValueOf(key: Key<out SetValue<E>>, element: Set<E>): SetValue.Immutable<E>
        = Value.immutableOf(key, element)

inline fun <E> immutableValueOf(key: Supplier<out Key<out SetValue<E>>>, element: Set<E>): SetValue.Immutable<E>
        = Value.immutableOf(key, element)

inline fun <K, V> immutableValueOf(key: Key<out MapValue<K, V>>, element: Map<K, V>): MapValue.Immutable<K, V>
        = Value.immutableOf(key, element)

inline fun <K, V> immutableValueOf(key: Supplier<out Key<out MapValue<K, V>>>, element: Map<K, V>): MapValue.Immutable<K, V>
        = Value.immutableOf(key, element)

inline fun <E> immutableValueOf(key: Key<out WeightedCollectionValue<E>>, element: WeightedTable<E>):
        WeightedCollectionValue.Immutable<E> = Value.immutableOf(key, element)

inline fun <E> immutableValueOf(key: Supplier<out Key<out WeightedCollectionValue<E>>>, element: WeightedTable<E>):
        WeightedCollectionValue.Immutable<E> = Value.immutableOf(key, element)

inline fun <E> mutableValueOf(key: Key<out Value<E>>, element: E): Value.Mutable<E>
        = Value.mutableOf(key, element)

inline fun <E> mutableValueOf(key: Supplier<out Key<out Value<E>>>, element: E): Value.Mutable<E>
        = Value.mutableOf(key, element)

inline fun <E> mutableValueOf(key: Key<out BoundedValue<E>>, element: E): BoundedValue.Mutable<E>
        = BoundedValue.mutableOf(key, element)

inline fun <E> mutableValueOf(key: Supplier<out Key<out BoundedValue<E>>>, element: E): BoundedValue.Mutable<E>
        = BoundedValue.mutableOf(key, element)

inline fun <E> mutableValueOf(key: Key<out ListValue<E>>, element: List<E>): ListValue.Mutable<E>
        = Value.mutableOf(key, element)

inline fun <E> mutableValueOf(key: Supplier<out Key<out ListValue<E>>>, element: List<E>): ListValue.Mutable<E>
        = Value.mutableOf(key, element)

inline fun <E> mutableValueOf(key: Key<out SetValue<E>>, element: Set<E>): SetValue.Mutable<E>
        = Value.mutableOf(key, element)

inline fun <E> mutableValueOf(key: Supplier<out Key<out SetValue<E>>>, element: Set<E>): SetValue.Mutable<E>
        = Value.mutableOf(key, element)

inline fun <K, V> mutableValueOf(key: Key<out MapValue<K, V>>, element: Map<K, V>): MapValue.Mutable<K, V>
        = Value.mutableOf(key, element)

inline fun <K, V> mutableValueOf(key: Supplier<out Key<out MapValue<K, V>>>, element: Map<K, V>): MapValue.Mutable<K, V>
        = Value.mutableOf(key, element)

inline fun <E> mutableValueOf(key: Key<out WeightedCollectionValue<E>>, element: WeightedTable<E>): WeightedCollectionValue.Mutable<E>
        = Value.mutableOf(key, element)

inline fun <E> mutableValueOf(key: Supplier<out Key<out WeightedCollectionValue<E>>>, element: WeightedTable<E>): WeightedCollectionValue.Mutable<E>
        = Value.mutableOf(key, element)
