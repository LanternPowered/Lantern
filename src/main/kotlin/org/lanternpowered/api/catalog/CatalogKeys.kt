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
package org.lanternpowered.api.catalog

import org.lanternpowered.api.Sponge
import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.ext.*
import org.lanternpowered.api.plugin.PluginContainer

object CatalogKeys {

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
    fun activePlugin(value: String): CatalogKey {
        val plugin: PluginContainer = CauseStack.current().first()
                ?: throw IllegalStateException("No plugin found in the cause stack.")
        return CatalogKey(plugin.id, value)
    }

    /**
     * Creates a named catalog key with the namespace of the active [PluginContainer].
     *
     * @param value The value
     * @param name The name
     * @return A new catalog key
     */
    @JvmStatic
    fun activePlugin(value: String, name: String): CatalogKey {
        val plugin: PluginContainer = CauseStack.current().first()
                ?: throw IllegalStateException("No plugin found in the cause stack.")
        return CatalogKey(plugin.id, value)
    }

    /**
     * Creates a catalog key.
     *
     * @param namespace The namespace
     * @param value The value
     * @return A new catalog key
     */
    @JvmStatic
    fun of(namespace: String, value: String): CatalogKey = CatalogKey(namespace, value)

    /**
     * Creates a catalog key with the namespace of lantern.
     *
     * @param value The value
     * @return A new catalog key
     */
    @JvmStatic
    fun lantern(value: String): CatalogKey = CatalogKey(LANTERN_NAMESPACE, value)

    /**
     * Creates a catalog key with the namespace of sponge.
     *
     * @param value The value
     * @return A new catalog key
     */
    @JvmStatic
    fun sponge(value: String): CatalogKey = CatalogKey(SPONGE_NAMESPACE, value)

    /**
     * Creates a named catalog key with the namespace of minecraft.
     *
     * @param value The value
     * @return A new catalog key
     */
    @JvmStatic
    fun minecraft(value: String): CatalogKey = CatalogKey(MINECRAFT_NAMESPACE, value)

    /**
     * Resolves a catalog key from the given value.
     */
    @JvmStatic
    fun resolve(value: String): CatalogKey {
        return Sponge.getRegistry().resolveKey(value) as CatalogKey
    }
}
