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
package org.lanternpowered.api

import org.lanternpowered.api.key.Namespace
import org.lanternpowered.api.key.NamespacedKey

@Suppress("NOTHING_TO_INLINE")
object NamespacedKeys {

    /**
     * Creates a new [NamespacedKey].
     *
     * @param namespace The namespace
     * @param value The value
     * @return The namespaced key
     */
    @JvmStatic
    inline fun of(namespace: String, value: String): NamespacedKey = NamespacedKey.of(namespace, value)

    /**
     * Creates a new [NamespacedKey].
     *
     * @param namespace The namespace
     * @param value The value
     * @return The namespaced key
     */
    @JvmStatic
    fun of(namespace: Namespace, value: String): NamespacedKey = of(namespace.name, value)

    /**
     * Creates a catalog key with the namespace of lantern.
     *
     * @param value The value
     * @return A new catalog key
     */
    @JvmStatic
    fun lantern(value: String): NamespacedKey = of(Namespace.LANTERN, value)

    /**
     * Creates a catalog key with the namespace of sponge.
     *
     * @param value The value
     * @return A new catalog key
     */
    @JvmStatic
    fun sponge(value: String): NamespacedKey = of(Namespace.SPONGE, value)

    /**
     * Creates a named catalog key with the namespace of minecraft.
     *
     * @param value The value
     * @return A new catalog key
     */
    @JvmStatic
    fun minecraft(value: String): NamespacedKey = of(Namespace.MINECRAFT, value)

    /**
     * Resolves a catalog key from the given value.
     */
    @JvmStatic
    fun resolve(value: String): NamespacedKey = NamespacedKey.resolve(value)
}
