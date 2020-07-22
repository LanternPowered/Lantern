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
package org.lanternpowered.server.catalog

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.lanternKey
import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.api.key.spongeKey
import org.spongepowered.api.NamedCatalogType

open class DefaultCatalogType(key: NamespacedKey) : AbstractCatalogType() {

    private val key: NamespacedKey

    init {
        check(key.namespace.isNotEmpty()) { "plugin id (key namespace) cannot be empty" }
        check(key.value.isNotEmpty()) { "id (key value) cannot be empty" }
        this.key = key
    }

    override fun getKey() = this.key

    companion object {

        fun minecraft(id: String) = DefaultCatalogType(minecraftKey(id))
        fun minecraft(id: String, name: String) = Named(minecraftKey(id), name)
        fun minecraft(id: String, name: (() -> String)) = Named(minecraftKey(id), name)

        fun sponge(id: String) = DefaultCatalogType(spongeKey(id))
        fun sponge(id: String, name: String): DefaultCatalogType = Named(spongeKey(id), name)
        fun sponge(id: String, name: () -> String): DefaultCatalogType = Named(spongeKey(id), name)

        fun lantern(id: String) = DefaultCatalogType(lanternKey(id))
        fun lantern(id: String, name: String): DefaultCatalogType = Named(lanternKey(id), name)
        fun lantern(id: String, name: () -> String): DefaultCatalogType = Named(lanternKey(id), name)
    }

    open class Named(key: NamespacedKey, name: () -> String) : DefaultCatalogType(key), NamedCatalogType {

        constructor(key: NamespacedKey, name: String): this(key, { name })

        private val theName: String by lazy(name)

        override fun getName() = this.theName
    }
}
