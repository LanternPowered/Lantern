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
@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package org.lanternpowered.api.ext

import org.lanternpowered.api.util.Tuple

// Deconstructing declaration support for tuples
operator fun <K, V> Tuple<K, V>.component1(): K = first
operator fun <K, V> Tuple<K, V>.component2(): V = second

fun <K, V> Tuple<K, V>.toPair() = Pair(first, second)
fun <K, V> Pair<K, V>.toTuple() = Tuple(first, second)

inline fun <T> Any?.uncheckedCast(): T = this as T

inline fun <T> T?.ifNotNull(fn: (T) -> Unit) {
    if (this != null) {
        fn(this)
    }
}

inline fun <T, R> T?.mapIfNotNull(fn: (T) -> R): R? {
    if (this != null) {
        return fn(this)
    }
    return null
}
