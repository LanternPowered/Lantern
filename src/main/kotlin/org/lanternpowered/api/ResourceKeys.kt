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

import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.first
import org.lanternpowered.api.plugin.PluginContainer

object ResourceKeys {

    /**
     * The sponge namespace.
     */
    const val SPONGE_NAMESPACE = "sponge"

    /**
     * The minecraft namespace.
     */
    const val MINECRAFT_NAMESPACE = "minecraft"

    /**
     * The lantern namespace.
     */
    const val LANTERN_NAMESPACE = "lantern"

    /**
     * Creates a catalog key with the namespace of the active [PluginContainer].
     *
     * @param value The value
     * @return A new catalog key
     */
    @JvmStatic
    fun activePlugin(value: String): ResourceKey {
        val plugin: PluginContainer = CauseStack.current().first()
                ?: throw IllegalStateException("No plugin found in the cause stack.")
        return resourceKeyOf(plugin.metadata.id, value)
    }

    /**
     * Creates a named catalog key with the namespace of the active [PluginContainer].
     *
     * @param value The value
     * @param name The name
     * @return A new catalog key
     */
    @JvmStatic
    fun activePlugin(value: String, name: String): ResourceKey {
        val plugin: PluginContainer = CauseStack.current().first()
                ?: throw IllegalStateException("No plugin found in the cause stack.")
        return resourceKeyOf(plugin.metadata.id, value)
    }

    /**
     * Creates a catalog key.
     *
     * @param namespace The namespace
     * @param value The value
     * @return A new catalog key
     */
    @JvmStatic
    fun of(namespace: String, value: String): ResourceKey = resourceKeyOf(namespace, value)

    /**
     * Creates a catalog key with the namespace of lantern.
     *
     * @param value The value
     * @return A new catalog key
     */
    @JvmStatic
    fun lantern(value: String): ResourceKey = resourceKeyOf(LANTERN_NAMESPACE, value)

    /**
     * Creates a catalog key with the namespace of sponge.
     *
     * @param value The value
     * @return A new catalog key
     */
    @JvmStatic
    fun sponge(value: String): ResourceKey = resourceKeyOf(SPONGE_NAMESPACE, value)

    /**
     * Creates a named catalog key with the namespace of minecraft.
     *
     * @param value The value
     * @return A new catalog key
     */
    @JvmStatic
    fun minecraft(value: String): ResourceKey = resourceKeyOf(MINECRAFT_NAMESPACE, value)

    /**
     * Resolves a catalog key from the given value.
     */
    @JvmStatic
    fun resolve(value: String): ResourceKey {
        return ResourceKey.resolve(value) as ResourceKey
    }
}
