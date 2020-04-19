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

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.catalog.CatalogKeys
import org.lanternpowered.api.text.translation.Translation
import org.spongepowered.api.NamedCatalogType

open class DefaultCatalogType(key: CatalogKey) : AbstractCatalogType() {

    private val key: CatalogKey

    init {
        check(key.namespace.isNotEmpty()) { "plugin id (key namespace) cannot be empty" }
        check(key.value.isNotEmpty()) { "id (key value) cannot be empty" }
        this.key = key
    }

    override fun getKey() = this.key

    companion object {

        fun minecraft(id: String) = DefaultCatalogType(CatalogKeys.minecraft(id))
        fun minecraft(id: String, name: String) = Named(CatalogKeys.minecraft(id), name)
        fun minecraft(id: String, name: (() -> String)) = Named(CatalogKeys.minecraft(id), name)

        fun sponge(id: String) = DefaultCatalogType(CatalogKeys.sponge(id))
        fun sponge(id: String, name: String): DefaultCatalogType = Named(CatalogKeys.sponge(id), name)
        fun sponge(id: String, name: () -> String): DefaultCatalogType = Named(CatalogKeys.sponge(id), name)

        fun lantern(id: String) = DefaultCatalogType(CatalogKeys.lantern(id))
        fun lantern(id: String, name: String): DefaultCatalogType = Named(CatalogKeys.lantern(id), name)
        fun lantern(id: String, name: () -> String): DefaultCatalogType = Named(CatalogKeys.lantern(id), name)
    }

    open class Named(key: CatalogKey, name: () -> String) : DefaultCatalogType(key), NamedCatalogType {

        constructor(key: CatalogKey, name: Translation): this(key, name::get)
        constructor(key: CatalogKey, name: String): this(key, { name })

        private val theName: String by lazy(name)

        override fun getName() = this.theName
    }
}
