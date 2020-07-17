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
import org.lanternpowered.api.plugin.PluginContainer
import org.lanternpowered.api.plugin.id
import org.spongepowered.api.ResourceKey.Builder as ResourceKeyBuilder

class LanternResourceKeyBuilder : ResourceKeyBuilder {

    private var namespace: String? = null
    private var value: String? = null

    override fun namespace(namespace: String): ResourceKeyBuilder = apply { this.namespace = namespace }
    override fun namespace(container: PluginContainer): ResourceKeyBuilder = namespace(container.id)
    override fun value(value: String): ResourceKeyBuilder = apply { this.value = value }

    override fun build(): ResourceKey {
        val namespace = checkNotNull(this.namespace) { "The namespace must be set" }
        val value = checkNotNull(this.value) { "The value must be set" }
        return LanternResourceKey(namespace, value)
    }

    override fun reset(): ResourceKeyBuilder = apply {
        this.namespace = null
        this.value = null
    }
}
