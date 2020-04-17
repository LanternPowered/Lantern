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
package org.lanternpowered.server.data

import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value
import java.util.function.Supplier

/**
 * Represents the global registry of [Key]s.
 */
interface GlobalKeyRegistry : KeyRegistry<GlobalKeyRegistration<*,*>> {

    /**
     * Gets the [GlobalKeyRegistration] for the given [Key], if present.
     *
     * @param key The key to get the registration for
     * @return The global key registration, if present
     */
    override operator fun <V : Value<E>, E : Any> get(key: Key<V>): GlobalKeyRegistration<V, E>?

    /**
     * Gets the [GlobalKeyRegistration] for the given [Key], if present.
     *
     * @param key The key to get the registration for
     * @return The global key registration, if present
     */
    override operator fun <V : Value<E>, E : Any> get(key: Supplier<out Key<V>>): GlobalKeyRegistration<V, E>? = get(key.get())

    /**
     * Gets the registration or registers the given [Key].
     *
     * @param key The key to register
     * @return The global key registration
     */
    fun <V : Value<E>, E : Any> getOrRegister(key: Key<V>): GlobalKeyRegistration<V, E>

    /**
     * Gets the registration or registers the given [Key].
     *
     * @param key The key to register
     * @return The global key registration
     */
    fun <V : Value<E>, E : Any> getOrRegister(key: Supplier<out Key<V>>): GlobalKeyRegistration<V, E> = getOrRegister(key.get())

    /**
     * Registers the given [Key].
     *
     * @param key The key to register
     * @return The global key registration
     */
    fun <V : Value<E>, E : Any> register(key: Key<V>): GlobalKeyRegistration<V, E>

    /**
     * Registers the given [Key].
     *
     * @param key The key to register
     * @return The global key registration
     */
    fun <V : Value<E>, E : Any> register(key: Supplier<out Key<V>>): GlobalKeyRegistration<V, E> = register(key.get())

    /**
     * The implementation of this registry.
     */
    companion object : GlobalKeyRegistry by LanternGlobalKeyRegistry()
}
