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
