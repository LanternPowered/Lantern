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
package org.lanternpowered.api.injector

import org.lanternpowered.api.Game
import org.lanternpowered.api.util.Named
import org.lanternpowered.api.util.type.TypeToken
import org.lanternpowered.api.util.type.typeTokenOf
import kotlin.reflect.KClass
import kotlin.reflect.KType

fun test() {
    val injector = Game.injector.createChild {
        bind<String>().toSingleton("Test")
        bind<@Named("name") String>().toSingleton("Named Test")
        bind<Int>().to { 1 }
    }

    injector.use {
        val value = inject<Int>()
        val named: @Named("name") String = inject()
        val test: Int by lazyInject()
    }
}

/**
 * Injects an object for the given type [T].
 *
 * It's also possible to annotate the type [T].
 *
 * @param T The type to inject
 * @return The injected object
 * @throws IllegalArgumentException If the given type [T] isn't supported
 * @throws IllegalStateException If not enough information could be found in the current execution context
 */
inline fun <reified T : Any> inject(): T = Game.injector.get(typeTokenOf())

/**
 * Injects an object for the given [type].
 *
 * @param type The type to inject
 * @return The injected object
 * @throws IllegalArgumentException If the given [type] isn't supported
 * @throws IllegalStateException If not enough information could be found in the current execution context
 */
fun <T : Any> inject(type: KClass<T>): T = Game.injector.get(type)

/**
 * Injects an object for the given [type].
 *
 * @param type The type to inject
 * @return The injected object
 * @throws IllegalArgumentException If the given [type] isn't supported
 * @throws IllegalStateException If not enough information could be found in the current execution context
 */
fun <T : Any> inject(type: TypeToken<T>): T = Game.injector.get(type)

/**
 * Injects an object for the given type [T].
 *
 * It's also possible to annotate the type [T].
 *
 * @param T The type to inject
 * @return The injected object
 * @throws IllegalArgumentException If the given type [T] isn't supported
 * @throws IllegalStateException If not enough information could be found in the current execution context
 */
inline fun <reified T : Any> lazyInject(): Lazy<T> = Game.injector.getLazy()

/**
 * Injects an object for the given [type].
 *
 * @param type The type to inject
 * @return The injected object
 * @throws IllegalArgumentException If the given [type] isn't supported
 * @throws IllegalStateException If not enough information could be found in the current execution context
 */
fun <T : Any> lazyInject(type: KClass<T>): Lazy<T> = Game.injector.getLazy(type)

/**
 * Injects an object for the given [type].
 *
 * @param type The type to inject
 * @return The injected object
 * @throws IllegalArgumentException If the given [type] isn't supported
 * @throws IllegalStateException If not enough information could be found in the current execution context
 */
fun <T : Any> lazyInject(type: TypeToken<T>): Lazy<T> = Game.injector.getLazy(type)

/**
 * Gets an injection for the given type [T].
 *
 * It's also possible to annotate the type [T].
 *
 * @param T The type to inject
 * @return The injected object
 * @throws IllegalArgumentException If the given type [T] isn't supported
 * @throws IllegalStateException If not enough information could be found in the current execution context
 */
inline fun <reified T : Any> Injector.get(): T = get(typeTokenOf())

/**
 * Gets a lazy injection for the given type [T].
 *
 * It's also possible to annotate the type [T].
 *
 * @param T The type to inject
 * @return The lazy injected object
 * @throws IllegalArgumentException If the given type [T] isn't supported
 * @throws IllegalStateException If not enough information could be found in the current execution context
 */
inline fun <reified T : Any> Injector.getLazy(): Lazy<T> = getLazy(typeTokenOf())

/**
 * Uses this injector to execute the given [block]. This means that while the block
 * is being executed, all injections will go through this injector instead of the
 * global one. This should be used in combination with child injectors.
 *
 * @param block The block to execute
 * @return The result of the block
 */
fun <R> Injector.use(block: () -> R): R = openContext().use { block() }

/**
 * Represents an injector which will provide injections
 * for lantern plugins. Injections will always be provided
 * for the current context. For example if a plugin is
 * constructing, then injections will be for this plugin.
 * Or if a plugin is handling events, and something gets
 * injected while doing so, the injection will target the
 * executing plugin.
 */
interface Injector {

    /**
     * Creates a child injector.
     *
     * @param fn The injector builder function
     * @return The child injector
     */
    fun createChild(fn: InjectorBuilder.() -> Unit): Injector

    /**
     * An injection context.
     */
    interface Context : AutoCloseable

    /**
     * Opens a new injection [Context]. While the context is active, all injections
     * will go through this injector instead of the global one. This should be used
     * in combination with child injectors.
     *
     * @return The context
     */
    fun openContext(): Context

    /**
     * Gets an injection for the given [type].
     *
     * @param type The type to inject
     * @return The object
     * @throws IllegalArgumentException If the given [type] isn't supported
     * @throws IllegalStateException If not enough information could be found in the current execution context
     */
    fun <T : Any> get(type: KType): T

    /**
     * Gets an injection for the given [type].
     *
     * @param type The type to inject
     * @return The object
     * @throws IllegalArgumentException If the given [type] isn't supported
     * @throws IllegalStateException If not enough information could be found in the current execution context
     */
    fun <T : Any> get(type: KClass<T>): T

    /**
     * Gets an injection for the given [type].
     *
     * @param type The type to inject
     * @return The object
     * @throws IllegalArgumentException If the given [type] isn't supported
     * @throws IllegalStateException If not enough information could be found in the current execution context
     */
    fun <T : Any> get(type: TypeToken<T>): T

    /**
     * Gets a lazy injection for the given [type].
     *
     * @param type The type to inject
     * @return The lazy object
     * @throws IllegalArgumentException If the given [type] isn't supported
     * @throws IllegalStateException If not enough information could be found in the current execution context
     */
    fun <T : Any> getLazy(type: KType): Lazy<T>

    /**
     * Gets a lazy injection for the given [type].
     *
     * @param type The type to inject
     * @return The lazy object
     * @throws IllegalArgumentException If the given [type] isn't supported
     * @throws IllegalStateException If not enough information could be found in the current execution context
     */
    fun <T : Any> getLazy(type: KClass<T>): Lazy<T>

    /**
     * Gets a lazy injection for the given [type].
     *
     * @param type The type to inject
     * @return The lazy object
     * @throws IllegalArgumentException If the given [type] isn't supported
     * @throws IllegalStateException If not enough information could be found in the current execution context
     */
    fun <T : Any> getLazy(type: TypeToken<T>): Lazy<T>
}

/**
 * Binds the given type [T].
 *
 * It's also possible to annotate the type [T], calling
 * [AnnotatedBindingBuilder.annotatedWith] will override
 * this.
 *
 * @param T The type to bind
 * @return The annotated binding builder
 */
inline fun <reified T> InjectorBuilder.bind(): AnnotatedBindingBuilder<T> = bind(typeTokenOf())

/**
 * A builder.
 */
interface InjectorBuilder {

    fun <T> bind(type: TypeToken<T>): AnnotatedBindingBuilder<T>

    fun <T> bind(type: KType): AnnotatedBindingBuilder<T>

    fun <T : Any> bind(type: KClass<T>): AnnotatedBindingBuilder<T>
}

/**
 * A builder to construct bindings.
 */
interface BindingBuilder<T> {

    fun toSingleton(instance: T)

    fun toSingleton(supplier: () -> T)

    fun to(supplier: () -> T)
}

/**
 * A builder to construct annotated bindings.
 */
interface AnnotatedBindingBuilder<T> : BindingBuilder<T> {

    fun annotatedWith(annotation: Annotation): BindingBuilder<T>

    fun annotatedWith(annotation: KClass<out Annotation>): BindingBuilder<T>
}
