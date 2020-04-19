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

import org.lanternpowered.api.util.collections.asUnmodifiableCollection
import org.lanternpowered.api.util.uncheckedCast
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value

internal class LanternGlobalKeyRegistry : GlobalKeyRegistry {

    private val map = mutableMapOf<Key<*>, GlobalKeyRegistration<*, *>>()

    override val registrations = this.map.values.asUnmodifiableCollection()
    override val keys = this.map.keys.asUnmodifiableCollection()

    override fun <V : Value<E>, E : Any> get(key: Key<V>): GlobalKeyRegistration<V, E>? = this.map[key].uncheckedCast()

    private fun checkRegistration(key: Key<*>) {
        check(key !in this.map) { "The key ${key.key} is already registered." }
    }

    override fun <V : Value<E>, E : Any> getOrRegister(key: Key<V>): GlobalKeyRegistration<V, E> {
        return get(key) ?: register(key)
    }

    override fun <V : Value<E>, E : Any> register(key: Key<V>): GlobalKeyRegistration<V, E> {
        checkRegistration(key)
        val registration = LanternGlobalKeyRegistration(key)
        this.map[key] = registration
        return registration
    }
}
