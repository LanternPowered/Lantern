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
package org.lanternpowered.api.registry

import org.lanternpowered.api.Lantern
import kotlin.reflect.KClass

/**
 * Provides a factory used to create instances of the specified type.
 *
 * @param T The type of factory
 * @throws UnknownTypeException If the type provided has not been registered
 * @return The factory
 */
inline fun <reified T : Any> factoryOf(): T = FactoryRegistry.provideFactory()

/**
 * Provides a factory used to create instances of the specified type.
 *
 * @param T The type of factory
 * @throws UnknownTypeException If the type provided has not been registered
 * @return The factory
 */
inline fun <reified T : Any> FactoryRegistry.provideFactory(): T = provideFactory(T::class)

/**
 * The factory registry.
 */
interface FactoryRegistry : org.spongepowered.api.registry.FactoryRegistry {

    /**
     * Provides a factory used to create instances of the specified type
     *
     * @param clazz The factory class
     * @param T The type of factory
     * @throws UnknownTypeException If the type provided has not been registered
     * @return The factory
     */
    fun <T : Any> provideFactory(clazz: KClass<T>): T = provideFactory(clazz)

    /**
     * Provides a factory used to create instances of the specified type
     *
     * @param clazz The factory class
     * @param T The type of factory
     * @throws UnknownTypeException If the type provided has not been registered
     * @return The factory
     */
    override fun <T : Any> provideFactory(clazz: Class<T>): T

    /**
     * The singleton instance of the factory registry.
     */
    companion object : FactoryRegistry by Lantern.registry.factoryRegistry
}