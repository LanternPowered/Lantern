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

import com.google.common.collect.ImmutableList
import com.google.common.collect.ImmutableMap
import com.google.common.collect.ImmutableMultimap
import com.google.common.collect.ImmutableSet
import com.google.common.collect.Multimap
import org.lanternpowered.api.util.collect.NonNullMutableList
import org.lanternpowered.api.util.collect.UnmodifiableIterable
import org.lanternpowered.api.util.collect.UnmodifiableIterator
import java.util.Collections
import java.util.EnumMap

inline fun <T> Iterable<T>.toImmutableList(): ImmutableList<T> = ImmutableList.copyOf(this)
inline fun <T> Array<T>.toImmutableList(): ImmutableList<T> = ImmutableList.copyOf(this)

inline fun <T> Iterable<T>.toImmutableSet(): ImmutableSet<T> = ImmutableSet.copyOf(this)
inline fun <T> Array<T>.toImmutableSet(): ImmutableSet<T> = ImmutableSet.copyOf(this)

inline fun <K, V> Map<K, V>.toImmutableMap(): ImmutableMap<K, V> = ImmutableMap.copyOf(this)
inline fun <K, V> Multimap<K, V>.toImmutableMultimap(): ImmutableMultimap<K, V> = ImmutableMultimap.copyOf(this)

inline fun immutableListOf(vararg args: Any) = args.asList().toImmutableList()
inline fun immutableSetOf(vararg args: Any) = args.asList().toImmutableSet()

inline fun <T> immutableListBuilderOf() = ImmutableList.builder<T>()
inline fun <T> immutableSetBuilderOf() = ImmutableSet.builder<T>()

inline fun <reified K : Enum<K>, V> enumMapOf(): MutableMap<K, V> = EnumMap(K::class.java)

inline fun <E> List<E>.asNonNullList(): List<E> = NonNullMutableList(this as MutableList<E>)
@JvmName("toMutableNonNullList")
inline fun <E> MutableList<E>.asNonNullList(): MutableList<E> = NonNullMutableList(this)

inline fun <E> singletonListOf(element: E): List<E> = Collections.singletonList(element)
inline fun <E> singletonSetOf(element: E): Set<E> = Collections.singleton(element)

// Unmodifiable views

/**
 * Creates a unmodifiable [Collection] view for this collection.
 */
inline fun <K, V> Map<K, V>.asUnmodifiable(): Map<K, V> = Collections.unmodifiableMap(this)

/**
 * Creates a unmodifiable [Collection] view for this collection.
 */
inline fun <T> Collection<T>.asUnmodifiable(): Collection<T> = Collections.unmodifiableCollection(this)

/**
 * Creates a unmodifiable [Set] view for this set.
 */
inline fun <T> Set<T>.asUnmodifiable(): Set<T> = Collections.unmodifiableSet(this)

/**
 * Creates a unmodifiable [List] view for this list.
 */
inline fun <T> List<T>.asUnmodifiable(): List<T> = Collections.unmodifiableList(this)

/**
 * Creates a unmodifiable [Iterable] view for this iterable.
 */
inline fun <T> Iterable<T>.asUnmodifiable(): Iterable<T> = UnmodifiableIterable(this)

/**
 * Creates a unmodifiable [Iterator] view for this iterator.
 */
inline fun <T> Iterator<T>.asUnmodifiable(): Iterator<T> = UnmodifiableIterator(this)

// Random

fun <E> Collection<E>.pickRandom(): E? {
    val size = this.size
    if (size == 0) {
        return null
    }
    val index = random.nextInt(size)
    forEachIndexed { i, element ->
        if (i == index) {
            return element
        }
    }
    throw IllegalStateException("Should never be reached")
}
