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

package org.lanternpowered.api.util.collections

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Multimap
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.util.weighted.WeightedTable
import java.util.Collections
import java.util.EnumMap
import java.util.LinkedList
import java.util.WeakHashMap
import java.util.stream.Stream

/**
 * Converts this [Iterable] into a [ImmutableList].
 */
inline fun <T> Iterable<T>.toImmutableList(): ImmutableList<T> = ImmutableList.copyOf(this)

/**
 * Converts this [Array] into a [ImmutableList].
 */
inline fun <T> Array<T>.toImmutableList(): ImmutableList<T> = ImmutableList.copyOf(this)

/**
 * Converts this [IntArray] into a [ImmutableList].
 */
fun IntArray.toImmutableList(): ImmutableList<Int> = ImmutableList.builder<Int>().apply { forEach { add(it) } }.build()

/**
 * Converts this [DoubleArray] into a [ImmutableList].
 */
fun DoubleArray.toImmutableList(): ImmutableList<Double> = ImmutableList.builder<Double>().apply { forEach { add(it) } }.build()

/**
 * Converts this [LongArray] into a [ImmutableList].
 */
fun LongArray.toImmutableList(): ImmutableList<Long> = ImmutableList.builder<Long>().apply { forEach { add(it) } }.build()

/**
 * Converts this [Iterable] into a [ImmutableSet].
 */
inline fun <T> Iterable<T>.toImmutableSet(): ImmutableSet<T> = ImmutableSet.copyOf(this)

/**
 * Converts this [Array] into a [ImmutableSet].
 */
inline fun <T> Array<T>.toImmutableSet(): ImmutableSet<T> = ImmutableSet.copyOf(this)

/**
 * Converts this [IntArray] into a [ImmutableSet].
 */
fun IntArray.toImmutableSet(): ImmutableSet<Int> = ImmutableSet.builder<Int>().apply { forEach { add(it) } }.build()

/**
 * Converts this [DoubleArray] into a [ImmutableSet].
 */
fun DoubleArray.toImmutableSet(): ImmutableSet<Double> = ImmutableSet.builder<Double>().apply { forEach { add(it) } }.build()

/**
 * Converts this [LongArray] into a [ImmutableSet].
 */
fun LongArray.toImmutableSet(): ImmutableSet<Long> = ImmutableSet.builder<Long>().apply { forEach { add(it) } }.build()

inline fun <K, V> Map<K, V>.toImmutableMap(): ImmutableMap<K, V> = ImmutableMap.copyOf(this)
inline fun <K, V> Multimap<K, V>.toImmutableMultimap(): ImmutableMultimap<K, V> = ImmutableMultimap.copyOf(this)

inline fun <T> immutableListOf(): ImmutableList<T> = ImmutableList.of()

inline fun <T> immutableListOf(vararg args: T) = args.asList().toImmutableList()
inline fun <T> immutableSetOf(vararg args: T) = args.asList().toImmutableSet()

inline fun <T> immutableListBuilderOf(): ImmutableList.Builder<T> = ImmutableList.builder<T>()
inline fun <T> immutableSetBuilderOf(): ImmutableSet.Builder<T> = ImmutableSet.builder<T>()

inline fun <K, V> immutableMapOf(): ImmutableMap<K, V> = ImmutableMap.of()
inline fun <K, V> immutableMapBuilderOf(): ImmutableMap.Builder<K, V> = ImmutableMap.builder()

inline fun <reified K : Enum<K>, V> enumMapOf(): MutableMap<K, V> = EnumMap(K::class.java)

/**
 * Constructs a new [MutableSet] with weak values.
 */
fun <E> weakSetOf(): MutableSet<E> = Collections.newSetFromMap(WeakHashMap())

/**
 * Constructs a new [MutableSet] with weak values from the given [iterable].
 */
fun <E> weakSetOf(iterable: Iterable<E>): MutableSet<E> = weakSetOf<E>().apply { addAll(iterable) }

/**
 * Constructs a new [MutableSet] with weak values from the given [args].
 */
fun <E> weakSetOf(vararg args: E): MutableSet<E> = weakSetOf<E>().apply { addAll(args) }

fun <E> immutableWeakSetOf(): Set<E> = Collections.unmodifiableSet(weakSetOf())
fun <E> immutableWeakSetOf(iterable: Iterable<E>): Set<E> = Collections.unmodifiableSet(weakSetOf<E>().apply { addAll(iterable) })
fun <E> immutableWeakSetOf(vararg args: E): Set<E> = Collections.unmodifiableSet(weakSetOf<E>().apply { addAll(args) })

/**
 * Returns a [ImmutableList] containing all elements produced by this stream.
 */
inline fun <T> Stream<T>.toImmutableList(): ImmutableList<T> = collect(ImmutableList.toImmutableList())

/**
 * Returns a [ImmutableSet] containing all elements produced by this stream.
 */
inline fun <T> Stream<T>.toImmutableSet(): ImmutableSet<T> = collect(ImmutableSet.toImmutableSet())

/**
 * Returns this collection as a unmodifiable view.
 */
inline fun <E> Collection<E>.asUnmodifiableCollection(): Collection<E> = Collections.unmodifiableCollection(this)

/**
 * Returns this list as a unmodifiable view.
 */
inline fun <E> List<E>.asUnmodifiableList(): List<E> = Collections.unmodifiableList(this)

/**
 * Returns this set as a unmodifiable view.
 */
inline fun <E> Set<E>.asUnmodifiableSet(): Set<E> = Collections.unmodifiableSet(this)

/**
 * Returns this set as a unmodifiable view.
 */
inline fun <K, V> Map<K, V>.asUnmodifiableMap(): Map<K, V> = Collections.unmodifiableMap(this)

/**
 * Gets whether all the elements of the [Iterable] are present in this [Collection].
 */
fun <E> Collection<E>.containsAll(iterable: Iterable<E>): Boolean {
    if (iterable is Collection<*>) {
        iterable as Collection<E>
        return containsAll(iterable)
    }
    for (element in iterable) {
        if (element !in this) {
            return false
        }
    }
    return true
}

/**
 * Removes all the keys of the [Iterable] from this [MutableMap].
 *
 * @param iterable The iterable with keys to remove
 * @return Whether the map was modified
 */
fun <K> MutableMap<K, *>.removeAll(iterable: Iterable<K>): Boolean {
    var modified = false
    for (key in iterable) {
        if (remove(key) != null) {
            modified = true
        }
    }
    return modified
}

/**
 * Removes all the map entries from the [Map] from this [MutableMap].
 *
 * @param map The map with entries to remove
 * @return Whether the map was modified
 */
fun <K, V> MutableMap<K, V>.removeAll(map: Map<out K, V>): Boolean {
    var modified = false
    for ((key, value) in map.entries) {
        modified = remove(key, value) or modified
    }
    return modified
}

/**
 * Attempts to create a [MutableCollection] of the given [collectionType].
 */
fun <C : MutableCollection<E>, E> mutableCollectionOf(collectionType: Class<C>): C {
    return when {
        LinkedHashSet::class.java.isAssignableFrom(collectionType) -> LinkedHashSet<Any>()
        HashSet::class.java.isAssignableFrom(collectionType) -> HashSet<Any>()
        Set::class.java.isAssignableFrom(collectionType) -> HashSet<Any>()
        ArrayList::class.java.isAssignableFrom(collectionType) -> ArrayList<Any>()
        LinkedList::class.java.isAssignableFrom(collectionType) -> LinkedList<Any>()
        WeightedTable::class.java.isAssignableFrom(collectionType) -> WeightedTable<Any>()
        else -> ArrayList<Any>()
    }.uncheckedCast()
}
