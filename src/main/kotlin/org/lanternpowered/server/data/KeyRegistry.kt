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
 * Represents a base registry of [Key]s.
 */
interface KeyRegistry<R : KeyRegistration<*,*>> {

    /**
     * A collection with all the [KeyRegistration]s.
     */
    val registrations: Collection<R>

    /**
     * A collection with all the registered [Key]s.
     */
    val keys: Collection<Key<*>>

    /**
     * Gets the [KeyRegistration] for the given [Key], if present.
     *
     * @param key The key to get the registration for
     * @return The key registration, if present
     */
    operator fun <V : Value<E>, E : Any> get(key: Key<V>): R?

    /**
     * Gets the [KeyRegistration] for the given [Key], if present.
     *
     * @param key The key to get the registration for
     * @return The key registration, if present
     */
    operator fun <V : Value<E>, E : Any> get(key: Supplier<out Key<V>>): R? = get(key.get())
}
