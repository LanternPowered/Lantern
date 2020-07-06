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

import com.google.common.collect.ImmutableSet
import org.lanternpowered.api.util.sequences.collect
import java.util.stream.Stream
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Converts this [Stream] into an [ImmutableSet].
 */
inline fun <T> Sequence<T>.toImmutableSet(): ImmutableSet<T> = collect(ImmutableSet.toImmutableSet())

/**
 * Converts this [Stream] into an [ImmutableSet].
 */
inline fun <T> Stream<T>.toImmutableSet(): ImmutableSet<T> = collect(ImmutableSet.toImmutableSet())

/**
 * Converts this [Iterable] into an [ImmutableSet].
 */
inline fun <T> Iterable<T>.toImmutableSet(): ImmutableSet<T> = ImmutableSet.copyOf(this)

/**
 * Converts this [Array] into an [ImmutableSet].
 */
inline fun <T> Array<T>.toImmutableSet(): ImmutableSet<T> = ImmutableSet.copyOf(this)

/**
 * Converts this [IntArray] into an [ImmutableSet].
 */
fun IntArray.toImmutableSet(): ImmutableSet<Int> = ImmutableSet.builder<Int>().apply { forEach { add(it) } }.build()

/**
 * Converts this [DoubleArray] into an [ImmutableSet].
 */
fun DoubleArray.toImmutableSet(): ImmutableSet<Double> = ImmutableSet.builder<Double>().apply { forEach { add(it) } }.build()

/**
 * Converts this [LongArray] into an [ImmutableSet].
 */
fun LongArray.toImmutableSet(): ImmutableSet<Long> = ImmutableSet.builder<Long>().apply { forEach { add(it) } }.build()

/**
 * Gets an empty [ImmutableSet].
 */
inline fun <T> immutableSetOf(): ImmutableSet<T> = ImmutableSet.of()

/**
 * Constructs a new [ImmutableSet] with the given values.
 */
inline fun <T> immutableSetOf(vararg values: T) = values.asList().toImmutableSet()

/**
 * Constructs a new [ImmutableSet] builder.
 */
inline fun <T> immutableSetBuilderOf(): ImmutableSet.Builder<T> = ImmutableSet.builder<T>()

/**
 * Constructs a new [ImmutableSet].
 */
inline fun <T> buildImmutableSet(fn: ImmutableSet.Builder<T>.() -> Unit): ImmutableSet<T> {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    return ImmutableSet.builder<T>().apply(fn).build()
}

/**
 * Constructs a new [ImmutableSet].
 */
inline fun <T> buildImmutableSet(capacity: Int, fn: ImmutableSet.Builder<T>.() -> Unit): ImmutableSet<T> {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    return ImmutableSet.builderWithExpectedSize<T>(capacity).apply(fn).build()
}
