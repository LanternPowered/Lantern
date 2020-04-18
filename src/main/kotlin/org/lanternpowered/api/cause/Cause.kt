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

/**
 * Constructs a new [Cause].
 */
fun cause(fn: CauseBuilder.() -> Unit): Cause = builderOf<CauseBuilder>().apply(fn).build()

/**
 * Constructs a new cause context.
 */
fun causeContext(fn: CauseContextBuilder.() -> Unit): CauseContext = CauseContext.builder().apply(fn).build()

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
