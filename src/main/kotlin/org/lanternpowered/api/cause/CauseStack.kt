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
package org.lanternpowered.api.cause

import java.util.Optional
import java.util.function.Supplier
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.reflect.KClass

/**
 * Executes the [block] with the given cause applied to this [CauseStack].
 */
inline fun CauseStack.withCause(cause: Any, block: () -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    try {
        pushCause(cause)
        block()
    } finally {
        popCause()
    }
}

/**
 * Executes the [block] with the given causes applied to this [CauseStack].
 */
inline fun CauseStack.withCauses(iterable: Iterable<Any>, block: () -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    var count = 0
    try {
        for (cause in iterable) {
            pushCause(cause)
            count++
        }
        block()
    } finally {
        popCauses(count)
    }
}

/**
 * Executes the [block] with the given causes applied to this [CauseStack].
 */
inline fun CauseStack.withCauses(first: Any, second: Any, vararg more: Any, block: () -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    var count = 2
    try {
        pushCause(first)
        pushCause(second)
        for (cause in more) {
            pushCause(cause)
            count++
        }
        block()
    } finally {
        popCauses(count)
    }
}

/**
 * Executes the [block] with a new [CauseStack.Frame]. It is automatically
 * closed after the block finishes executing.
 */
inline fun CauseStack.withFrame(block: (frame: CauseStack.Frame) -> Unit) {
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
inline fun <reified T : Any> CauseStack.first(): T? = first(T::class)

/**
 * Gets the last object instance of the [Cause] of type [T].
 *
 * @param T The type of object being queried for
 * @return The last element of the type, if available
 */
inline fun <reified T : Any> CauseStack.last(): T? = last(T::class)

/**
 * Returns whether the target type matches any object of this [Cause].
 *
 * @param T The target type
 * @return True if found, false otherwise
 */
inline fun <reified T : Any> CauseStack.containsType(): Boolean = containsType(T::class)

/**
 * A [CauseStack] for a specific [Thread].
 */
interface CauseStack : org.spongepowered.api.event.CauseStackManager {

    /**
     * Gets the context value with the given key.
     *
     * @param key The context key
     * @param T The type of the value stored with the event context key
     * @return The context object, if present
     */
    operator fun <T : Any> get(key: Supplier<out CauseContextKey<T>>): T? = get(key.get())

    /**
     * Gets the context value with the given key.
     *
     * @param key The context key
     * @param T The type of the value stored with the event context key
     * @return The context object, if present
     */
    operator fun <T : Any> get(key: CauseContextKey<T>): T?

    /**
     * Adds the given object to the current context under the given key.
     *
     * @param key The context key
     * @param value The object
     * @param T The type of the value stored with the event context key
     */
    operator fun <T : Any> set(key: Supplier<out CauseContextKey<T>>, value: T) = set(key.get(), value)

    /**
     * Adds the given object to the current context under the given key.
     *
     * @param key The context key
     * @param value The object
     * @param T The type of the value stored with the event context key
     */
    operator fun <T : Any> set(key: CauseContextKey<T>, value: T)

    /**
     * Gets the first [T] object of this [Cause], if available.
     *
     * @param target The class of the target type
     * @param T The type of object being queried for
     * @return The first element of the type, if available
     */
    fun <T : Any> first(target: KClass<T>): T?

    /**
     * Gets the first [T] object of this [Cause], if available.
     *
     * @param target The class of the target type
     * @param T The type of object being queried for
     * @return The first element of the type, if available
     */
    fun <T : Any> first(target: Class<T>): Optional<T>

    /**
     * Gets the last object instance of the [Cause] of type [T].
     *
     * @param target The class of the target type
     * @param T The type of object being queried for
     * @return The last element of the type, if available
     */
    fun <T : Any> last(target: KClass<T>): T?

    /**
     * Gets the last object instance of the [Cause] of type [T].
     *
     * @param target The class of the target type
     * @param T The type of object being queried for
     * @return The last element of the type, if available
     */
    fun <T : Any> last(target: Class<T>): Optional<T>

    /**
     * Returns whether the target class matches any object of this [Cause].
     *
     * @param target The class of the target type
     * @return True if found, false otherwise
     */
    fun containsType(target: KClass<*>): Boolean

    /**
     * Returns whether the target class matches any object of this [Cause].
     *
     * @param target The class of the target type
     * @return True if found, false otherwise
     */
    fun containsType(target: Class<*>): Boolean

    /**
     * Checks if this cause contains of any of the provided [Object]. This
     * is the equivalent to checking based on [Any.equals] for each
     * object in this cause.
     *
     * @param any The object to check if it is contained
     * @return True if the object is contained within this cause
     */
    operator fun contains(any: Any): Boolean

    override fun pushCauseFrame(): Frame
    override fun pushCause(obj: Any): CauseStack
    override fun <T : Any> addContext(key: CauseContextKey<T>, value: T): CauseStack

    interface Frame : org.spongepowered.api.event.CauseStackManager.StackFrame {

        override fun pushCause(obj: Any): Frame
        override fun <T : Any> addContext(key: CauseContextKey<T>, value: T): Frame
    }

    companion object {

        /**
         * Gets the current [Cause].
         */
        val currentCause: Cause
            get() = this.current().currentCause

        /**
         * Gets the [CauseStack] for the current [Thread].
         * `null` will be returned if the thread isn't supported.
         *
         * @return The cause stack
         */
        @JvmStatic
        fun currentOrNull() = CauseStackManager.currentStackOrNull()

        /**
         * Gets the [CauseStack] for the current [Thread]. A
         * empty [CauseStack] will be returned if the thread
         * isn't supported.
         *
         * @return The cause stack
         */
        @JvmStatic
        fun currentOrEmpty() = CauseStackManager.currentStackOrEmpty()

        /**
         * Gets the [CauseStack] for the current [Thread]. A
         * [IllegalStateException] will be thrown if the current thread
         * isn't supported.
         *
         * @return The cause stack
         */
        @JvmStatic
        fun current() = CauseStackManager.currentStack()
    }
}
