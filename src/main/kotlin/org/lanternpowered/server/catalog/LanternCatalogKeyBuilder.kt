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
import org.lanternpowered.api.catalog.CatalogKeyBuilder
import org.lanternpowered.api.plugin.PluginContainer

class LanternCatalogKeyBuilder : CatalogKeyBuilder {

    private var namespace: String? = null
    private var value: String? = null

    override fun namespace(namespace: String): CatalogKeyBuilder = apply { this.namespace = namespace }
    override fun namespace(container: PluginContainer): CatalogKeyBuilder = namespace(container.id)
    override fun value(value: String): CatalogKeyBuilder = apply { this.value = value }

    override fun build(): CatalogKey {
        val namespace = checkNotNull(this.namespace) { "The namespace must be set" }
        val value = checkNotNull(this.value) { "The value must be set" }
        return LanternCatalogKey(namespace, value)
    }

    override fun reset(): CatalogKeyBuilder = apply {
        this.namespace = null
        this.value = null
    }
}
