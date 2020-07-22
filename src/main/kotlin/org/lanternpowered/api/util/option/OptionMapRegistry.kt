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
@file:Suppress("UNCHECKED_CAST")

package org.lanternpowered.api.util.option

import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.api.util.collections.toImmutableList
import java.util.concurrent.ConcurrentHashMap

/**
 * The registry for a specific [OptionMap] subclass.
 *
 * @property mapType The map type this registry holds options for
 */
class OptionMapRegistry<T : OptionMapType> internal constructor(val mapType: Class<T>) {

    // All the options mapped by their key
    internal val byKey = HashMap<NamespacedKey, Option<T, *>>()

    /**
     * Gets all the options for the target map type.
     */
    fun all(): Collection<Option<T, *>> = this.byKey.values.toImmutableList()

    /**
     * Gets whether the given [NamespacedKey] is present within this registry.
     */
    operator fun contains(key: NamespacedKey): Boolean = this.byKey.containsKey(key)

    /**
     * Gets whether the given [Option] is present within this registry.
     */
    operator fun contains(option: Option<T, *>): Boolean = this.byKey.containsValue(option)

    /**
     * Gets the [Option] that is registered for the given [NamespacedKey].
     */
    operator fun get(key: NamespacedKey): Option<T, *>? = this.byKey[key]

    companion object {

        private val byMapType = ConcurrentHashMap<Class<*>, OptionMapRegistry<*>>()

        /**
         * Gets or constructs the [OptionMapRegistry] for the given [OptionMap] type.
         */
        fun <T : OptionMapType> of(mapType: Class<T>): OptionMapRegistry<T> =
                this.byMapType.computeIfAbsent(mapType) { OptionMapRegistry(mapType) } as OptionMapRegistry<T>
    }
}
