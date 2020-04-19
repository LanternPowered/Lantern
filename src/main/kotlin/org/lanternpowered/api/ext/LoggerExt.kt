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
@file:JvmName("LoggerHelper")
package org.lanternpowered.api.ext

import org.lanternpowered.api.logging.Logger
import org.lanternpowered.api.logging.Marker
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.TextRepresentable
import org.lanternpowered.api.text.toLegacy
import java.util.function.Supplier
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

// Logger extensions to allow lazily initialized messages
// and Text objects to be logged

/**
 * Converts [Any] into a readable [String].
 */
private fun toLoggableString(any: Any): String {
    if (any is TextRepresentable) {
        return any.toText().toLegacy()
    } else if (any is Supplier<*>) {
        return toLoggableString(any.get())
    }
    return any.toString()
}

/**
 * Converts [Any] provided by the function into a readable [String].
 */
private inline fun toLoggableString(fn: () -> Any): String = toLoggableString(fn())

/**
 * Logs the provided message at debug level.
 *
 * @param fn The message provider
 */
fun Logger.debug(fn: () -> Any) {
    contract {
        callsInPlace(fn, InvocationKind.AT_MOST_ONCE)
    }
    if (isDebugEnabled) debug(toLoggableString(fn))
}

/**
 * Logs the provided message at debug level for the given [Marker].
 *
 * @param marker The marker
 * @param fn The message provider
 */
fun Logger.debug(marker: Marker, fn: () -> Any) {
    contract {
        callsInPlace(fn, InvocationKind.AT_MOST_ONCE)
    }
    if (isDebugEnabled(marker)) debug(marker, toLoggableString(fn))
}

/**
 * Logs the provided message at trace level.
 *
 * @param fn The message provider
 */
fun Logger.trace(fn: () -> Any) {
    contract {
        callsInPlace(fn, InvocationKind.AT_MOST_ONCE)
    }
    if (isTraceEnabled) trace(toLoggableString(fn))
}

/**
 * Logs the provided message at trace level for the given [Marker].
 *
 * @param marker The marker
 * @param fn The message provider
 */
fun Logger.trace(marker: Marker, fn: () -> Any) {
    contract {
        callsInPlace(fn, InvocationKind.AT_MOST_ONCE)
    }
    if (isTraceEnabled(marker)) trace(marker, toLoggableString(fn))
}

/**
 * Logs the provided message at info level.
 *
 * @param fn The message provider
 */
fun Logger.info(fn: () -> Any) {
    contract {
        callsInPlace(fn, InvocationKind.AT_MOST_ONCE)
    }
    if (isInfoEnabled) info(toLoggableString(fn))
}

/**
 * Logs the provided message at info level for the given [Marker].
 *
 * @param marker The marker
 * @param fn The message provider
 */
fun Logger.info(marker: Marker, fn: () -> Any) {
    contract {
        callsInPlace(fn, InvocationKind.AT_MOST_ONCE)
    }
    if (isInfoEnabled(marker)) info(marker, toLoggableString(fn))
}

/**
 * Logs the provided message at warn level.
 *
 * @param fn The message provider
 */
fun Logger.warn(fn: () -> Any) {
    contract {
        callsInPlace(fn, InvocationKind.AT_MOST_ONCE)
    }
    if (isWarnEnabled) warn(toLoggableString(fn))
}

/**
 * Logs the provided message at warn level for the given [Marker].
 *
 * @param marker The marker
 * @param fn The message provider
 */
fun Logger.warn(marker: Marker, fn: () -> Any) {
    contract {
        callsInPlace(fn, InvocationKind.AT_MOST_ONCE)
    }
    if (isWarnEnabled(marker)) warn(marker, toLoggableString(fn))
}

/**
 * Logs the provided message at error level.
 *
 * @param fn The message provider
 */
fun Logger.error(fn: () -> Any) {
    contract {
        callsInPlace(fn, InvocationKind.AT_MOST_ONCE)
    }
    if (isErrorEnabled) error(toLoggableString(fn))
}

/**
 * Logs the provided message at error level for the given [Marker].
 *
 * @param marker The marker
 * @param fn The message provider
 */
fun Logger.error(marker: Marker, fn: () -> Any) {
    contract {
        callsInPlace(fn, InvocationKind.AT_MOST_ONCE)
    }
    if (isErrorEnabled(marker)) error(marker, toLoggableString(fn))
}

/**
 * Logs the text message at debug level.
 *
 * @param text The text message
 */
fun Logger.debug(text: Text) {
    if (isDebugEnabled) debug(text.toLegacy())
}

/**
 * Logs the text message at debug level for the given [Marker].
 *
 * @param marker The marker
 * @param text The text message
 */
fun Logger.debug(marker: Marker, text: Text) {
    if (isDebugEnabled(marker)) debug(marker, text.toLegacy())
}

/**
 * Logs the text message and [Throwable] at debug level.
 *
 * @param text The text message
 * @param throwable The throwable
 */
fun Logger.debug(text: Text, throwable: Throwable) {
    if (isDebugEnabled) debug(text.toLegacy(), throwable)
}

/**
 * Logs the text message and [Throwable] at debug level for the given [Marker].
 *
 * @param marker The marker
 * @param text The text message
 * @param throwable The throwable
 */
fun Logger.debug(marker: Marker, text: Text, throwable: Throwable) {
    if (isDebugEnabled(marker)) debug(marker, text.toLegacy(), throwable)
}

/**
 * Logs the text message at trace level.
 *
 * @param text The text message
 */
fun Logger.trace(text: Text) {
    if (isTraceEnabled) trace(text.toLegacy())
}

/**
 * Logs the text message at trace level for the given [Marker].
 *
 * @param marker The marker
 * @param text The text message
 */
fun Logger.trace(marker: Marker, text: Text) {
    if (isTraceEnabled(marker)) trace(marker, text.toLegacy())
}

/**
 * Logs the text message and [Throwable] at trace level.
 *
 * @param text The text message
 * @param throwable The throwable
 */
fun Logger.trace(text: Text, throwable: Throwable) {
    if (isTraceEnabled) trace(text.toLegacy(), throwable)
}

/**
 * Logs the text message and [Throwable] at trace level for the given [Marker].
 *
 * @param marker The marker
 * @param text The text message
 * @param throwable The throwable
 */
fun Logger.trace(marker: Marker, text: Text, throwable: Throwable) {
    if (isTraceEnabled(marker)) trace(marker, text.toLegacy(), throwable)
}

/**
 * Logs the text message at info level.
 *
 * @param text The text message
 */
fun Logger.info(text: Text) {
    if (isInfoEnabled) info(text.toLegacy())
}

/**
 * Logs the text message at info level for the given [Marker].
 *
 * @param marker The marker
 * @param text The text message
 */
fun Logger.info(marker: Marker, text: Text) {
    if (isInfoEnabled(marker)) info(marker, text.toLegacy())
}

/**
 * Logs the text message and [Throwable] at info level.
 *
 * @param text The text message
 * @param throwable The throwable
 */
fun Logger.info(text: Text, throwable: Throwable) {
    if (isInfoEnabled) info(text.toLegacy(), throwable)
}

/**
 * Logs the text message and [Throwable] at info level for the given [Marker].
 *
 * @param marker The marker
 * @param text The text message
 * @param throwable The throwable
 */
fun Logger.info(marker: Marker, text: Text, throwable: Throwable) {
    if (isInfoEnabled(marker)) info(marker, text.toLegacy(), throwable)
}

/**
 * Logs the text message at warn level.
 *
 * @param text The text message
 */
fun Logger.warn(text: Text) {
    if (isWarnEnabled) warn(text.toLegacy())
}

/**
 * Logs the text message at warn level for the given [Marker].
 *
 * @param marker The marker
 * @param text The text message
 */
fun Logger.warn(marker: Marker, text: Text) {
    if (isWarnEnabled(marker)) warn(marker, text.toLegacy())
}

/**
 * Logs the text message and [Throwable] at warn level.
 *
 * @param text The text message
 * @param throwable The throwable
 */
fun Logger.warn(text: Text, throwable: Throwable) {
    if (isWarnEnabled) warn(text.toLegacy(), throwable)
}

/**
 * Logs the text message and [Throwable] at warn level for the given [Marker].
 *
 * @param marker The marker
 * @param text The text message
 * @param throwable The throwable
 */
fun Logger.warn(marker: Marker, text: Text, throwable: Throwable) {
    if (isWarnEnabled(marker)) warn(marker, text.toLegacy(), throwable)
}

/**
 * Logs the text message at error level.
 *
 * @param text The text message
 */
fun Logger.error(text: Text) {
    if (isErrorEnabled) error(text.toLegacy())
}

/**
 * Logs the text message at error level for the given [Marker].
 *
 * @param marker The marker
 * @param text The text message
 */
fun Logger.error(marker: Marker, text: Text) {
    if (isErrorEnabled(marker)) error(marker, text.toLegacy())
}

/**
 * Logs the text message and [Throwable] at error level.
 *
 * @param text The text message
 * @param throwable The throwable
 */
fun Logger.error(text: Text, throwable: Throwable) {
    if (isErrorEnabled) error(text.toLegacy(), throwable)
}

/**
 * Logs the text message and [Throwable] at error level for the given [Marker].
 *
 * @param marker The marker
 * @param text The text message
 * @param throwable The throwable
 */
fun Logger.error(marker: Marker, text: Text, throwable: Throwable) {
    if (isErrorEnabled(marker)) error(marker, text.toLegacy(), throwable)
}
