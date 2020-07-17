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

import org.lanternpowered.api.registry.builderOf
import org.lanternpowered.api.registry.BaseBuilder
import org.lanternpowered.api.util.optional.orNull
import java.util.function.Supplier
import kotlin.reflect.KClass

/**
 * A cause represents the reason or initiator of an event.
 */
typealias Cause = org.spongepowered.api.event.cause.Cause
typealias CauseContextKey<T> = org.spongepowered.api.event.cause.EventContextKey<T>
typealias CauseContextKeys = org.spongepowered.api.event.cause.EventContextKeys
typealias CauseContext = org.spongepowered.api.event.cause.EventContext
typealias CauseContextBuilder = org.spongepowered.api.event.cause.EventContext.Builder

/**
 * Gets the first [T] object of this [Cause], if available.
 *
 * @param T The type of object being queried for
 * @return The first element of the type, if available
 */
inline fun <reified T : Any> Cause.first(): T? = first(T::class)

/**
 * Gets the first [T] object of this [Cause], if available.
 *
 * @param target The class of the target type
 * @param T The type of object being queried for
 * @return The first element of the type, if available
 */
fun <T : Any> Cause.first(target: KClass<T>): T? = first(target.java).orNull()

/**
 * Gets the last [T] object of this [Cause], if available.
 *
 * @param T The type of object being queried for
 * @return The last element of the type, if available
 */
inline fun <reified T : Any> Cause.last(): T? = last(T::class)

/**
 * Gets the last [T] object of this [Cause], if available.
 *
 * @param target The class of the target type
 * @param T The type of object being queried for
 * @return The last element of the type, if available
 */
fun <T : Any> Cause.last(target: KClass<T>): T? = last(target.java).orNull()

/**
 * Gets the object immediately before the object that is an instance of the given target.
 *
 * @param T The target type
 * @return The object
 */
inline fun <reified T : Any> Cause.before(): Any? = before(T::class)

/**
 * Gets the object immediately before the object that is an instance of the given target.
 *
 * @param target The class of the target type
 * @return The object
 */
fun Cause.before(target: KClass<*>): Any? = before(target.java).orNull()

/**
 * Gets the object immediately after the object that is an instance of the given target.
 *
 * @param T The target type
 * @return The object
 */
inline fun <reified T : Any> Cause.after(): Any? = after(T::class)

/**
 * Gets the object immediately after the object that is an instance of the given target.
 *
 * @param target The class of the target type
 * @return The object
 */
fun Cause.after(target: KClass<*>): Any? = after(target.java).orNull()

/**
 * Gets a list of all objects that are instances of the given type [T].
 *
 * @param T The type of objects to query for
 * @return A list of the objects queried
 */
inline fun <reified T : Any> Cause.allOf(): List<T> = allOf(T::class)

/**
 * Gets a list of all objects that are instances of the given type [T].
 *
 * @param target The class of the target type
 * @param T The type of objects to query for
 * @return A list of the objects queried
 */
fun <T : Any> Cause.allOf(target: KClass<T>): List<T> = allOf(target.java)

/**
 * Gets a list of all objects that are not instances of the given type [T].
 *
 * @param T The type of objects to query for
 * @return A list of the objects queried
 */
inline fun <reified T : Any> Cause.noneOf(): List<Any> = noneOf(T::class)

/**
 * Gets a list of all objects that are not instances of the given target type.
 *
 * @param target The class of the target type
 * @return A list of the objects queried
 */
fun Cause.noneOf(target: KClass<*>): List<Any> = noneOf(target.java)

private val emptyCause = Cause.of(CauseContext.empty(), emptyList())

/**
 * Gets an empty [Cause].
 */
fun emptyCause(): Cause = emptyCause

/**
 * Constructs a new [Cause].
 */
fun causeOf(cause: Any): Cause = Cause.of(CauseContext.empty(), cause)

/**
 * Constructs a new [Cause].
 */
fun causeOf(first: Any, vararg more: Any): Cause = Cause.of(CauseContext.empty(), listOf(first) + more.asList())

/**
 * Constructs a new [Cause].
 */
fun cause(block: CauseBuilder.() -> Unit): Cause = builderOf<CauseBuilder>().apply(block).build()

/**
 * Constructs a new [CauseContext].
 */
fun emptyCauseContext(): CauseContext = CauseContext.empty()

/**
 * Constructs a new [CauseContext].
 */
fun causeContextOf(entries: Map<CauseContextKey<*>, Any>): CauseContext = CauseContext.of(entries)

/**
 * Constructs a new [CauseContext].
 */
fun causeContext(block: CauseContextBuilder.() -> Unit): CauseContext = CauseContext.builder().apply(block).build()

/**
 * Constructs a new [Cause].
 */
fun Cause.withContext(context: CauseContext): Cause = Cause.of(context, all())

/**
 * A builder to construct [Cause]s.
 */
interface CauseBuilder : BaseBuilder<Cause, CauseBuilder> {

    /**
     * Appends the cause.
     */
    fun append(cause: Any): CauseBuilder

    /**
     * Appends all the causes.
     */
    fun appendAll(causes: Iterable<Any>): CauseBuilder

    /**
     * Sets the cause context. This overrides all the added contexts.
     */
    fun context(causeContext: CauseContext): CauseBuilder

    /**
     * Adds a context value.
     */
    fun <T> context(key: CauseContextKey<T>, value: T): CauseBuilder

    /**
     * Adds a context value.
     */
    fun <T> context(key: Supplier<out CauseContextKey<T>>, value: T): CauseBuilder = context(key.get(), value)

    /**
     * Builds the [Cause].
     */
    fun build(): Cause
}
