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

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.api.ResourceKeys
import org.lanternpowered.api.text.translation.Translation
import org.spongepowered.api.NamedCatalogType

open class DefaultCatalogType(key: ResourceKey) : AbstractCatalogType() {

    private val key: ResourceKey

    init {
        check(key.namespace.isNotEmpty()) { "plugin id (key namespace) cannot be empty" }
        check(key.value.isNotEmpty()) { "id (key value) cannot be empty" }
        this.key = key
    }

    override fun getKey() = this.key

    companion object {

        fun minecraft(id: String) = DefaultCatalogType(ResourceKeys.minecraft(id))
        fun minecraft(id: String, name: String) = Named(ResourceKeys.minecraft(id), name)
        fun minecraft(id: String, name: (() -> String)) = Named(ResourceKeys.minecraft(id), name)

        fun sponge(id: String) = DefaultCatalogType(ResourceKeys.sponge(id))
        fun sponge(id: String, name: String): DefaultCatalogType = Named(ResourceKeys.sponge(id), name)
        fun sponge(id: String, name: () -> String): DefaultCatalogType = Named(ResourceKeys.sponge(id), name)

        fun lantern(id: String) = DefaultCatalogType(ResourceKeys.lantern(id))
        fun lantern(id: String, name: String): DefaultCatalogType = Named(ResourceKeys.lantern(id), name)
        fun lantern(id: String, name: () -> String): DefaultCatalogType = Named(ResourceKeys.lantern(id), name)
    }

    open class Named(key: ResourceKey, name: () -> String) : DefaultCatalogType(key), NamedCatalogType {

        constructor(key: ResourceKey, name: Translation): this(key, name::get)
        constructor(key: ResourceKey, name: String): this(key, { name })

        private val theName: String by lazy(name)

        override fun getName() = this.theName
    }
}
