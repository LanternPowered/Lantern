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

import org.lanternpowered.api.data.KeyBuilder
import org.lanternpowered.api.x.data.XBoundedKeyBuilder
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.BoundedValue

/**
 * Sets the value range of the bounded value [Key].
 */
fun <V : BoundedValue<E>, E : Comparable<E>> KeyBuilder<V>.range(range: ClosedRange<E>): KeyBuilder<V> =
        minimum(range.start).maximum(range.endInclusive)

/**
 * Sets the minimum value of the bounded value [Key].
 */
fun <V : BoundedValue<E>, E : Any> KeyBuilder<V>.minimum(value: E): KeyBuilder<V> =
        apply { uncheckedCast<XBoundedKeyBuilder<V, E>>().setMinValue(value) }

/**
 * Sets the minimum value supplier of the bounded value [Key].
 */
fun <V : BoundedValue<E>, E : Any> KeyBuilder<V>.minimum(supplier: () -> E): KeyBuilder<V> =
        apply { uncheckedCast<XBoundedKeyBuilder<V, E>>().setMinValueSupplier(supplier) }

/**
 * Sets the maximum value of the bounded value [Key].
 */
fun <V : BoundedValue<E>, E : Any> KeyBuilder<V>.maximum(value: E): KeyBuilder<V> =
        apply { uncheckedCast<XBoundedKeyBuilder<V, E>>().setMaxValue(value) }

/**
 * Sets the maximum value supplier of the bounded value [Key].
 */
fun <V : BoundedValue<E>, E : Any> KeyBuilder<V>.maximum(supplier: () -> E): KeyBuilder<V> =
        apply { uncheckedCast<XBoundedKeyBuilder<V, E>>().setMaxValueSupplier(supplier) }

/**
 * Sets the comparator of the bounded value [Key].
 */
fun <V : BoundedValue<E>, E : Any> KeyBuilder<V>.comparator(comparator: Comparator<in E>): KeyBuilder<V> =
        apply { uncheckedCast<XBoundedKeyBuilder<V, E>>().setComparator(comparator) }
