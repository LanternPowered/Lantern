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