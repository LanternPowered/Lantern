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

import org.spongepowered.api.data.DataProvider
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.value.Value

/**
 * Represents a globally registered key.
 */
interface GlobalKeyRegistration<V : Value<E>, E : Any> : KeyRegistration<V, E> {

    /**
     * Adds a [DataProvider].
     *
     * @param provider The data provider
     * @return This registration, for chaining
     * @throws IllegalArgumentException If the target key doesn't match the key of this registration
     */
    fun addProvider(provider: DataProvider<V, E>): GlobalKeyRegistration<V, E>

    /**
     * Adds a [IDataProvider] that is built with the function.
     *
     * @param fn The builder function
     * @return This registration, for chaining
     */
    fun addProvider(fn: DataProviderBuilder<V, E>.(key: Key<V>) -> Unit): GlobalKeyRegistration<V, E>
}
