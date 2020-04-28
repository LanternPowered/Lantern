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
package org.lanternpowered.api.inject

import org.lanternpowered.api.Game
import org.lanternpowered.api.util.type.TypeToken
import org.lanternpowered.api.util.type.typeTokenOf
import kotlin.reflect.KClass

/**
 * Injects an object for the given type [T].
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
 * Gets an injection for the given type [T].
 *
 * @param T The type to inject
 * @return The injected object
 * @throws IllegalArgumentException If the given type [T] isn't supported
 * @throws IllegalStateException If not enough information could be found in the current execution context
 */
inline fun <reified T : Any> Injector.get(): T = get(typeTokenOf())

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
}
