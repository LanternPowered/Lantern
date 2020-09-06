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

import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.Multimap
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.util.weighted.WeightedTable
import java.util.Collections
import java.util.EnumMap
import java.util.LinkedList
import java.util.WeakHashMap
import java.util.concurrent.ConcurrentHashMap

inline fun <K, V> Map<K, V>.toImmutableMap(): ImmutableMap<K, V> = ImmutableMap.copyOf(this)
inline fun <K, V> Multimap<K, V>.toImmutableMultimap(): ImmutableMultimap<K, V> = ImmutableMultimap.copyOf(this)

/**
 * Constructs a new [ImmutableMap].
 */
inline fun <K, V> immutableMapOf(): ImmutableMap<K, V> = ImmutableMap.of()

/**
 * Constructs a new [ImmutableMap] with the given pairs.
 */
fun <K, V> immutableMapOf(vararg pairs: Pair<K, V>): ImmutableMap<K, V> =
        ImmutableMap.builderWithExpectedSize<K, V>(pairs.size).apply { pairs.forEach { (key, value) -> put(key, value) } }.build()

inline fun <K, V> immutableMapBuilderOf(): ImmutableMap.Builder<K, V> = ImmutableMap.builder()

inline fun <reified K : Enum<K>, V> enumMapOf(): MutableMap<K, V> = EnumMap(K::class.java)

/**
 * Constructs a new [MutableSet] with weak values.
 */
fun <E> weakHashSetOf(): MutableSet<E> = Collections.newSetFromMap(WeakHashMap())

/**
 * Constructs a new [MutableSet] with weak values from the given [elements].
 */
fun <E> weakHashSetOf(elements: Iterable<E>): MutableSet<E> = weakHashSetOf<E>().apply { addAll(elements) }

/**
 * Constructs a new [MutableSet] with weak values from the given [elements].
 */
fun <E> weakHashSetOf(vararg elements: E): MutableSet<E> = weakHashSetOf<E>().apply { addAll(elements) }

/**
 * Constructs a new empty immutable weak hash set.
 */
fun <E> immutableWeakSetOf(): Set<E> =
        Collections.unmodifiableSet(weakHashSetOf())

/**
 * Constructs a new immutable weak hash set with the given elements.
 */
fun <E> immutableWeakSetOf(iterable: Iterable<E>): Set<E> =
        Collections.unmodifiableSet(weakHashSetOf<E>().apply { addAll(iterable) })

/**
 * Constructs a new immutable weak hash set with the given elements.
 */
fun <E> immutableWeakSetOf(vararg args: E): Set<E> =
        Collections.unmodifiableSet(weakHashSetOf<E>().apply { addAll(args) })

/**
 * Constructs a new concurrent hash set.
 */
fun <E> concurrentHashSetOf(): MutableSet<E> =
        Collections.newSetFromMap(ConcurrentHashMap())

/**
 * Constructs a new concurrent hash set with the given initial elements.
 */
fun <E> concurrentHashSetOf(elements: Iterable<E>): MutableSet<E> =
        concurrentHashSetOf<E>().apply { addAll(elements) }

/**
 * Constructs a new concurrent hash set with the given initial elements.
 */
fun <E> concurrentHashSetOf(vararg elements: E): MutableSet<E> =
        concurrentHashSetOf<E>().apply { addAll(elements) }

/**
 * Constructs a new concurrent hash map.
 */
inline fun <K, V> concurrentHashMapOf(): MutableMap<K, V> = ConcurrentHashMap()

/**
 * Constructs a new concurrent hash map with the given initial pairs.
 */
fun <K, V> concurrentHashMapOf(vararg pairs: Pair<K, V>): MutableMap<K, V> = pairs.toMap(ConcurrentHashMap())

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
    if (iterable is Collection<*>)
        return containsAll(iterable as Collection<E>)
    return iterable.all { it in this }
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
        if (remove(key) != null)
            modified = true
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
    for ((key, value) in map.entries)
        modified = remove(key, value) or modified
    return modified
}

fun <E> MutableCollection<E>.removeFirst(predicate: (E) -> Boolean): E =
        this.removeFirstOrNull(predicate) ?: throw NoSuchElementException()

fun <E> MutableCollection<E>.removeFirstOrNull(predicate: (E) -> Boolean): E? {
    val it = this.iterator()
    while (it.hasNext()) {
        val element = it.next()
        if (predicate(element)) {
            it.remove()
            return element
        }
    }
    return null
}

fun <E> MutableCollection<E>.getAndRemoveAll(predicate: (E) -> Boolean): Collection<E> {
    val result = mutableListOf<E>()
    val it = this.iterator()
    while (it.hasNext()) {
        val element = it.next()
        if (predicate(element)) {
            it.remove()
            result += element
        }
    }
    return result
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
