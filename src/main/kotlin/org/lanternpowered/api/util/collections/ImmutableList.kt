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
import org.lanternpowered.api.util.sequences.collect
import java.util.stream.Stream
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Converts this [Stream] into an [ImmutableList].
 */
inline fun <T> Sequence<T>.toImmutableList(): ImmutableList<T> = collect(ImmutableList.toImmutableList())

/**
 * Converts this [Stream] into an [ImmutableList].
 */
inline fun <T> Stream<T>.toImmutableList(): ImmutableList<T> = collect(ImmutableList.toImmutableList())

/**
 * Converts this [Iterable] into an [ImmutableList].
 */
inline fun <T> Iterable<T>.toImmutableList(): ImmutableList<T> = ImmutableList.copyOf(this)

/**
 * Converts this [Array] into an [ImmutableList].
 */
inline fun <T> Array<T>.toImmutableList(): ImmutableList<T> = ImmutableList.copyOf(this)

/**
 * Converts this [IntArray] into an [ImmutableList].
 */
fun IntArray.toImmutableList(): ImmutableList<Int> = ImmutableList.builder<Int>().apply { forEach { add(it) } }.build()

/**
 * Converts this [DoubleArray] into an [ImmutableList].
 */
fun DoubleArray.toImmutableList(): ImmutableList<Double> = ImmutableList.builder<Double>().apply { forEach { add(it) } }.build()

/**
 * Converts this [LongArray] into an [ImmutableList].
 */
fun LongArray.toImmutableList(): ImmutableList<Long> = ImmutableList.builder<Long>().apply { forEach { add(it) } }.build()

/**
 * Gets an empty [ImmutableList].
 */
inline fun <T> immutableListOf(): ImmutableList<T> = ImmutableList.of()

/**
 * Constructs a new [ImmutableList] with the given values.
 */
inline fun <T> immutableListOf(vararg values: T) = values.asList().toImmutableList()

/**
 * Constructs a new [ImmutableList] builder.
 */
inline fun <T> immutableListBuilderOf(): ImmutableList.Builder<T> = ImmutableList.builder<T>()

/**
 * Constructs a new [ImmutableList].
 */
inline fun <T> buildImmutableList(fn: ImmutableList.Builder<T>.() -> Unit): ImmutableList<T> {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    return ImmutableList.builder<T>().apply(fn).build()
}

/**
 * Constructs a new [ImmutableList].
 */
inline fun <T> buildImmutableList(capacity: Int, fn: ImmutableList.Builder<T>.() -> Unit): ImmutableList<T> {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    return ImmutableList.builderWithExpectedSize<T>(capacity).apply(fn).build()
}
