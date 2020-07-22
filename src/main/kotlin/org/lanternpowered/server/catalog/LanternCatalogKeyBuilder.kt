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

import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.api.plugin.PluginContainer
import org.lanternpowered.api.plugin.id
import org.lanternpowered.api.namespace.NamespacedKey.Builder as NamespacedKeyBuilder

class LanternNamespacedKeyBuilder : NamespacedKeyBuilder {

    private var namespace: String? = null
    private var value: String? = null

    override fun namespace(namespace: String): NamespacedKeyBuilder = apply { this.namespace = namespace }
    override fun namespace(container: PluginContainer): NamespacedKeyBuilder = namespace(container.id)
    override fun value(value: String): NamespacedKeyBuilder = apply { this.value = value }

    override fun build(): NamespacedKey {
        val namespace = checkNotNull(this.namespace) { "The namespace must be set" }
        val value = checkNotNull(this.value) { "The value must be set" }
        return LanternNamespacedKey(namespace, value)
    }

    override fun reset(): NamespacedKeyBuilder = apply {
        this.namespace = null
        this.value = null
    }
}
