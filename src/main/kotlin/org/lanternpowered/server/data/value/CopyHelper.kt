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
@file:Suppress("UNCHECKED_CAST")

package org.lanternpowered.server.data.value

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableSet
import org.lanternpowered.server.util.copy.Copyable
import org.spongepowered.api.data.CopyableDataHolder
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.util.weighted.UnmodifiableWeightedTable
import org.spongepowered.api.util.weighted.WeightedTable
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.LinkedList
import java.util.Optional
import java.util.concurrent.ConcurrentSkipListSet
import java.util.concurrent.CopyOnWriteArrayList

object CopyHelper {

    /**
     * Creates a copy for the given object of type [T].
     *
     * @param value The object
     * @param <T> The object type
     * @return The copied object
     */
    fun <T> copy(value: T): T {
        return when (value) {
            is Copyable<*> -> (value as Copyable<*>).copy()
            is Map<*, *> -> copyMap(value as Map<Any, Any>)
            is List<*> -> copyList(value as List<Any>)
            is Set<*> -> copySet(value as Set<Any>)
            is WeightedTable<*> -> copyWeightedTable(value as WeightedTable<Any>)
            is CopyableDataHolder -> value.copy()
            is Optional<*> -> value.map { copy(it) }
            else -> value
        } as T
    }

    private fun Collection<*>.shouldCopyElements(): Boolean {
        return if (isNotEmpty()) {
            val first = first()
            copy(first) === first
        } else false
    }

    fun <W : WeightedTable<T>, T> copyWeightedTable(weightedTable: W): W {
        if (weightedTable is Copyable<*>)
            return (weightedTable as Copyable<*>).copy() as W
        val copyElements = weightedTable.shouldCopyElements()
        val type = weightedTable.javaClass
        val copy = WeightedTable<T>()
        if (copyElements) {
            weightedTable.forEach { element -> copy.add(copy(element)) }
        }
        if (type == UnmodifiableWeightedTable::class.java)
            return UnmodifiableWeightedTable(copy) as W
        return copy as W
    }

    fun <S : Set<E>, E> copySet(set: S): S {
        if (set is Copyable<*>)
            return (set as Copyable<*>).copy() as S
        val copyElements = set.shouldCopyElements()
        if (set is ImmutableSet<*>) {
            return if (copyElements) {
                set.stream().map { obj -> copy(obj) }.collect(ImmutableSet.toImmutableSet()) as S
            } else set
        }
        val copy: Set<*>
        val type = set.javaClass
        if (type == HashSet::class.java) {
            if (copyElements) {
                copy = HashSet<E>()
                set.forEach { element -> copy.add(copy(element)) }
            } else {
                copy = HashSet(set)
            }
        } else if (type == ConcurrentSkipListSet::class.java) {
            copy = if (copyElements) {
                ConcurrentSkipListSet<E>(set.asSequence()
                        .map { obj -> copy(obj) }
                        .toMutableList())
            } else {
                ConcurrentSkipListSet<E>(set)
            }
        } else {
            if (copyElements) {
                copy = LinkedHashSet<E>(set.size)
                set.forEach { element: E -> copy.add(copy(element)) }
            } else {
                copy = LinkedHashSet(set)
            }
        }
        return copy as S
    }

    fun <L : List<E>, E> copyList(list: L): L {
        if (list is Copyable<*>)
            return (list as Copyable<*>).copy() as L
        val copyElements = list.shouldCopyElements()
        if (list is ImmutableList<*>) {
            return if (copyElements) {
                list.stream().map { obj -> copy(obj) }.collect(ImmutableList.toImmutableList()) as L
            } else list
        }
        val copy: List<*>
        val type: Class<*> = list.javaClass
        if (type == LinkedList::class.java) {
            if (copyElements) {
                copy = LinkedList<E>()
                list.forEach { element: E -> copy.add(copy(element)) }
            } else {
                copy = LinkedList(list)
            }
        } else if (type == CopyOnWriteArrayList::class.java) {
            copy = if (copyElements) {
                CopyOnWriteArrayList<E>(list.asSequence()
                        .map { obj -> copy(obj) }
                        .toMutableList())
            } else {
                CopyOnWriteArrayList<E>(list)
            }
        } else {
            if (copyElements) {
                copy = ArrayList<E>(list.size)
                list.forEach { element: E -> copy.add(copy(element)) }
            } else {
                copy = ArrayList(list)
            }
        }
        return copy as L
    }

    fun <M : Map<K, V>, K, V> copyMap(map: M): M {
        if (map is Copyable<*>) {
            return (map as Copyable<*>).copy() as M
        }
        val copyEntries: Boolean
        copyEntries = if (map.isEmpty()) {
            false
        } else {
            val firstEntry = map.entries.iterator().next()
            copy(firstEntry.key) === firstEntry.key ||
                    copy(firstEntry.value) === firstEntry.value
        }
        if (map is ImmutableMap<*, *>) {
            if (copyEntries) {
                val builder = ImmutableMap.builderWithExpectedSize<K, V>(map.size)
                map.forEach { key: K, value: V -> builder.put(copy(key), copy(value)) }
                return builder.build() as M
            }
            return map
        }
        val copy: Map<* ,*>
        val type: Class<*> = map.javaClass
        if (type == HashMap::class.java) {
            if (copyEntries) {
                copy = HashMap<K, V>()
                map.forEach { key: K, value: V -> copy.put(copy(key), copy(value)) }
            } else {
                copy = HashMap(map)
            }
        } else {
            if (copyEntries) {
                copy = LinkedHashMap<K, V>()
                map.forEach { key: K, value: V -> copy.put(copy(key), copy(value)) }
            } else {
                copy = LinkedHashMap(map)
            }
        }
        return copy as M
    }

    /**
     * Creates a supplier which creates copies of the provided value, if needed.
     *
     * @param value The value to create the supplier from
     * @param <T> The value type
     * @return The constructed supplier
     * */
    fun <T> createSupplier(value: T): () -> T {
        val copy = copy(value)
        return if (copy === value) {
            { value }
        } else {
            { copy(copy) }
        }
    }
}
