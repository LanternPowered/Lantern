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

package org.lanternpowered.api.cause

import org.lanternpowered.api.util.optional.orNull
import java.util.Optional
import java.util.function.Supplier
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass

typealias CauseStackManager = org.spongepowered.api.event.CauseStackManager
typealias CauseStackManagerFrame = org.spongepowered.api.event.CauseStackManager.StackFrame

/**
 * Executes the [block] with the given causes applied to the current stack.
 */
inline fun CauseStackManager.withCauses(iterable: Iterable<Any>, block: () -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val causeStack = this as? CauseStack ?: CauseStack.current()
    causeStack.withCauses(iterable) {
        block()
    }
}

/**
 * Executes the [block] with the given causes applied to the current stack.
 */
inline fun CauseStackManager.withCauses(first: Any, vararg more: Any, block: () -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    val causeStack = this as? CauseStack ?: CauseStack.current()
    causeStack.withCauses(first, *more) {
        block()
    }
}

/**
 * Executes the [block] with a new [CauseStackManagerFrame]. It is automatically
 * closed after the block finishes executing.
 */
inline fun CauseStackManager.withFrame(block: CauseStackManagerFrame.() -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    pushCauseFrame().use(block)
}

/**
 * Gets the first [T] object of this [Cause], if available.
 *
 * @param T The type of object being queried for
 * @return The first element of the type, if available
 */
inline fun <reified T : Any> CauseStackManager.first(): T? = first(T::class)

/**
 * Gets the first [T] object of this [Cause], if available.
 *
 * @param target The class of the target type
 * @param T The type of object being queried for
 * @return The first element of the type, if available
 */
fun <T : Any> CauseStackManager.first(target: KClass<T>): T? =
        if (this is CauseStack) first(target) else this.currentCause.first(target.java).orNull()

/**
 * Gets the first [T] object of this [Cause], if available.
 *
 * @param target The class of the target type
 * @param T The type of object being queried for
 * @return The first element of the type, if available
 */
fun <T : Any> CauseStackManager.first(target: Class<T>): Optional<T> =
        if (this is CauseStack) first(target) else this.currentCause.first(target)

/**
 * Gets the last [T] object of this [Cause], if available.
 *
 * @param T The type of object being queried for
 * @return The first element of the type, if available
 */
inline fun <reified T : Any> CauseStackManager.last(): T? = last(T::class)

/**
 * Gets the last [T] object of this [Cause], if available.
 *
 * @param target The class of the target type
 * @param T The type of object being queried for
 * @return The first element of the type, if available
 */
fun <T : Any> CauseStackManager.last(target: KClass<T>): T? =
        if (this is CauseStack) last(target) else this.currentCause.last(target.java).orNull()

/**
 * Gets the last [T] object of this [Cause], if available.
 *
 * @param target The class of the target type
 * @param T The type of object being queried for
 * @return The first element of the type, if available
 */
fun <T : Any> CauseStackManager.last(target: Class<T>): Optional<T> =
        if (this is CauseStack) last(target) else this.currentCause.last(target)

/**
 * Returns whether the target type matches any object of this [Cause].
 *
 * @param T The target type
 * @return True if found, false otherwise
 */
inline fun <reified T> CauseStackManager.containsType(): Boolean = containsType(T::class)

/**
 * Returns whether the target class matches any object of this [Cause].
 *
 * @param target The class of the target type
 * @return True if found, false otherwise
 */
fun CauseStackManager.containsType(target: KClass<*>): Boolean =
        if (this is CauseStack) containsType(target) else this.currentCause.containsType(target.java)

/**
 * Returns whether the target class matches any object of this [Cause].
 *
 * @param target The class of the target type
 * @return True if found, false otherwise
 */
fun CauseStackManager.containsType(target: Class<*>): Boolean =
        if (this is CauseStack) containsType(target) else this.currentCause.containsType(target)

/**
 * Checks if this cause contains of any of the provided [Any]. This
 * is the equivalent to checking based on [equals] for each
 * object in this cause.
 *
 * @param any The object to check if it is contained
 * @return True if the object is contained within this cause
 */
inline operator fun CauseStackManager.contains(any: Any): Boolean =
        if (this is CauseStack) contains(any) else this.currentCause.contains(any)

/**
 * Gets the context value with the given key.
 *
 * @param key The context key
 * @param T The type of the value stored with the event context key
 * @return The context object, if present
 */
inline operator fun <T> CauseStackManager.get(key: Supplier<out CauseContextKey<T>>): T? = getContext(key).orNull()

/**
 * Gets the context value with the given key.
 *
 * @param key The context key
 * @param T The type of the value stored with the event context key
 * @return The context object, if present
 */
inline operator fun <T> CauseStackManager.get(key: CauseContextKey<T>): T? = getContext(key).orNull()

/**
 * Adds the given object to the current context under the given key.
 *
 * @param key The context key
 * @param value The object
 * @param T The type of the value stored with the event context key
 */
inline operator fun <T> CauseStackManager.set(key: Supplier<out CauseContextKey<T>>, value: T) { addContext(key, value) }

/**
 * Adds the given object to the current context under the given key.
 *
 * @param key The context key
 * @param value The object
 * @param T The type of the value stored with the event context key
 */
inline operator fun <T> CauseStackManager.set(key: CauseContextKey<T>, value: T) { addContext(key, value) }
