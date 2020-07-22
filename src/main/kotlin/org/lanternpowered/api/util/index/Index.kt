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
package org.lanternpowered.api.util.index

typealias Index<K, V> = net.kyori.adventure.util.Index<K, V>

fun <K, V> indexOf(values: Array<V>, function: (value: V) -> K): Index<K, V> =
        indexOf(values.asList(), function)

fun <K, V> indexOf(first: V, second: V, vararg more: V, function: (value: V) -> K): Index<K, V> =
        indexOf(listOf(first, second) + more.asList(), function)

fun <K, V> indexOf(values: Iterable<V>, function: (value: V) -> K): Index<K, V> =
        Index.create(function, values.toList())

fun <K, V> indexOf(values: Array<V>, function: (index: Int, value: V) -> K): Index<K, V> =
        indexOf(values.asList(), function)

fun <K, V> indexOf(first: V, second: V, vararg more: V, function: (index: Int, value: V) -> K): Index<K, V> =
        indexOf(listOf(first, second) + more.asList(), function)

fun <K, V> indexOf(values: Iterable<V>, function: (index: Int, value: V) -> K): Index<K, V> {
    return Index.create(object : java.util.function.Function<V, K> {
        private var index = 0
        override fun apply(value: V): K = function(this.index++, value)
    }, values.toList())
}

fun <K, V> Index<K, V>.requireValue(key: K): V = value(key) ?: error("The key $key isn't mapped to a value.")
fun <K, V> Index<K, V>.requireKey(value: V): K = key(value) ?: error("The value $value isn't mapped to a key.")
