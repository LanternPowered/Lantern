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
package org.lanternpowered.server.data.value

import com.google.common.collect.BiMap
import com.google.common.collect.HashBiMap
import com.google.common.collect.ImmutableBiMap
import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import org.lanternpowered.api.ext.*
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.util.weighted.WeightedTable
import java.util.ArrayList
import java.util.HashMap
import java.util.HashSet
import java.util.LinkedHashMap
import java.util.LinkedHashSet
import java.util.LinkedList
import java.util.Optional
import java.util.TreeSet

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
            is Set<*> -> return copySetAsMutable(value.uncheckedCast<Set<Any>>()).uncheckedCast()
            is List<*> -> return copyListAsMutable(value.uncheckedCast<List<Any>>()).uncheckedCast()
            is Map<*, *> -> return copyAsMutable(value.uncheckedCast<Map<Any, Any>>()).uncheckedCast()
            is WeightedTable<*> -> return copyWeightedTable(value.uncheckedCast<WeightedTable<Any>>()).uncheckedCast()
            is ItemStack -> return value.copy().uncheckedCast()
            is Optional<*> -> return value.map { copy(it) }.uncheckedCast()
            else -> value
        }
    }

    fun <T> copySetAsMutable(set: Set<T>): MutableSet<T> {
        if (set is LinkedHashSet<*>) {
            return LinkedHashSet(set)
        } else if (set is TreeSet<*>) {
            return TreeSet<T>(set)
        }
        return HashSet(set)
    }

    fun <T> copyListAsMutable(list: List<T>): MutableList<T> {
        return if (list is LinkedList<*> || list is ImmutableList<*>) {
            LinkedList(list)
        } else ArrayList(list)
    }

    fun <K, V> copyAsMutable(map: Map<K, V>): MutableMap<K, V> {
        if (map is BiMap<*, *>) {
            return HashBiMap.create(map)
        } else if (map is LinkedHashMap<*, *>) {
            return LinkedHashMap(map)
        }
        return HashMap(map)
    }

    fun <T> copyWeightedTable(table: WeightedTable<T>): WeightedTable<T> {
        val copy = WeightedTable<T>(table.rolls)
        copy.addAll(table.entries)
        return copy
    }

    fun <K, V> mapAsUnmodifiable(map: Map<K, V>): ImmutableMap<K, V> {
        if (map is ImmutableMap<*, *>) {
            return map as ImmutableMap<K, V>
        } else if (map is BiMap<*, *>) {
            return ImmutableBiMap.copyOf(map)
        }
        return ImmutableMap.copyOf(map)
    }
}
