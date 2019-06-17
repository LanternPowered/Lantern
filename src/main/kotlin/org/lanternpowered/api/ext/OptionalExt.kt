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

import org.spongepowered.api.util.OptBool
import java.util.Optional
import java.util.OptionalDouble
import java.util.OptionalInt

/**
 * Gets a empty [Optional].
 */
inline fun <T> emptyOptional(): Optional<T> = Optional.empty()

/**
 * Gets a empty [OptionalInt].
 */
inline fun emptyOptionalInt(): OptionalInt = OptionalInt.empty()

/**
 * Gets a empty [OptionalDouble].
 */
inline fun emptyOptionalDouble(): OptionalDouble = OptionalDouble.empty()

/**
 * Unwraps the [Optional] value.
 */
inline fun <T> Optional<T>.orNull(): T? = orElse(null)

/**
 * Wraps the boolean value into an [Optional].
 */
inline fun Boolean.optional(): Optional<Boolean> = OptBool.of(this)

/**
 * Wraps the nullable boolean value into an [Optional].
 */
inline fun Boolean?.optional(): Optional<Boolean> = OptBool.of(this)

/**
 * Wraps the value into an [Optional]. Could be nullable
 * or not, there's no guarantees.
 */
@JvmName("optionalOfUnknown")
inline fun <T> T.optional(): Optional<T> = Optional.ofNullable(this)

/**
 * Wraps the value into an [Optional].
 */
@JvmName("optionalOf")
inline fun <T : Any> T.optional(): Optional<T> = Optional.of(this)

/**
 * Wraps the nullable value into an [Optional].
 */
@JvmName("optionalOfNullable")
inline fun <T : Any> T?.optional(): Optional<T> = Optional.ofNullable(this)

/**
 * Wraps the int into an [OptionalInt].
 */
@JvmName("optionalOf")
inline fun Int.optionalInt(): OptionalInt = OptionalInt.of(this)

/**
 * Wraps the int into an [OptionalInt].
 */
@JvmName("optionalOfNullable")
inline fun Int?.optionalInt(): OptionalInt = if (this == null) OptionalInt.empty() else OptionalInt.of(this)

/**
 * Wraps the double into an [OptionalDouble].
 */
@JvmName("optionalOf")
inline fun Double.optionalDouble(): OptionalDouble = OptionalDouble.of(this)

/**
 * Wraps the double into an [OptionalDouble].
 */
@JvmName("optionalOfNullable")
inline fun Double?.optionalDouble(): OptionalDouble = if (this == null) OptionalDouble.empty() else OptionalDouble.of(this)

/**
 * Transforms this [Optional] into a [OptionalInt].
 */
inline fun Optional<Int>.asInt(): OptionalInt = if (isPresent) OptionalInt.of(get()) else OptionalInt.empty()

/**
 * Transforms this [Optional] into a [OptionalDouble].
 */
inline fun Optional<Double>.asDouble(): OptionalDouble = if (isPresent) OptionalDouble.of(get()) else OptionalDouble.empty()
