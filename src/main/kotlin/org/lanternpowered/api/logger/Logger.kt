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
@file:Suppress("FunctionName", "NOTHING_TO_INLINE")

package org.lanternpowered.api.logger

import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.TextRepresentable
import org.lanternpowered.api.text.serializer.LegacyTextSerializer
import org.lanternpowered.api.text.text
import java.util.function.Supplier
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass

typealias Logger = org.slf4j.Logger
typealias LoggerFactory = org.slf4j.LoggerFactory
typealias Marker = org.slf4j.Marker

/**
 * Constructs a new [Logger] for the target class.
 *
 * @param clazz The target class
 * @return The constructed logger
 */
inline fun <T : Any> loggerOf(clazz: KClass<T>): Logger = LoggerFactory.getLogger(clazz.java)

/**
 * Constructs a new [Logger] with the specified name.
 *
 * @param name The name
 * @return The constructed logger
 */
inline fun loggerOf(name: String): Logger = LoggerFactory.getLogger(name)

private fun Text.toLegacy(): String = LegacyTextSerializer.serialize(this)

/**
 * Converts [Any] into a readable [String].
 */
private fun toLoggableString(any: Any): String {
    if (any is TextRepresentable) {
        return any.text().toLegacy()
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
