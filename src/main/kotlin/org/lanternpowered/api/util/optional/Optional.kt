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
@file:Suppress("NOTHING_TO_INLINE", "UNCHECKED_CAST")

package org.lanternpowered.api.util.optional

import org.spongepowered.api.util.OptBool
import java.util.Optional
import java.util.OptionalDouble
import java.util.OptionalInt
import java.util.OptionalLong

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
 * Gets a empty [OptionalLong].
 */
inline fun emptyOptionalLong(): OptionalLong = OptionalLong.empty()

/**
 * Unwraps the [Optional] value.
 */
inline fun <T> Optional<T>.orNull(): T? = this.orElse(null)

/**
 * Wraps the boolean value into an [Optional].
 */
inline fun Boolean.asOptional(): Optional<Boolean> = OptBool.of(this)

/**
 * Wraps the nullable boolean value into an [Optional].
 */
inline fun Boolean?.asOptional(): Optional<Boolean> = OptBool.of(this)

/**
 * Wraps the value into an [Optional].
 */
@JvmName("optionalOf")
inline fun <T : Any> T.asOptional(): Optional<T> = Optional.of(this)

/**
 * Wraps the nullable value into an [Optional].
 */
@JvmName("optionalOfNullable")
inline fun <T : Any> T?.asOptional(): Optional<T> = Optional.ofNullable(this)

/**
 * Wraps the int into an [OptionalInt].
 */
@JvmName("optionalOf")
inline fun Int.asOptionalInt(): OptionalInt = OptionalInt.of(this)

/**
 * Wraps the int into an [OptionalInt].
 */
@JvmName("optionalOfNullable")
inline fun Int?.asOptionalInt(): OptionalInt = if (this == null) OptionalInt.empty() else OptionalInt.of(this)

/**
 * Wraps the double into an [OptionalDouble].
 */
@JvmName("optionalOf")
inline fun Double.asOptionalDouble(): OptionalDouble = OptionalDouble.of(this)

/**
 * Wraps the double into an [OptionalDouble].
 */
@JvmName("optionalOfNullable")
inline fun Double?.asOptionalDouble(): OptionalDouble = if (this == null) OptionalDouble.empty() else OptionalDouble.of(this)

/**
 * Wraps the long into an [OptionalLong].
 */
@JvmName("optionalOf")
inline fun Long.asOptionalLong(): OptionalLong = OptionalLong.of(this)

/**
 * Wraps the long into an [OptionalLong].
 */
@JvmName("optionalOfNullable")
inline fun Long?.asOptionalLong(): OptionalLong = if (this == null) OptionalLong.empty() else OptionalLong.of(this)

/**
 * Transforms this [Optional] into a [OptionalInt].
 */
inline fun Optional<Int>.asInt(): OptionalInt = if (this.isPresent) OptionalInt.of(this.get()) else OptionalInt.empty()

/**
 * Transforms this [Optional] into a [OptionalDouble].
 */
inline fun Optional<Double>.asDouble(): OptionalDouble = if (this.isPresent) OptionalDouble.of(this.get()) else OptionalDouble.empty()
