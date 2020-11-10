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
package org.lanternpowered.server.data

import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.first
import org.lanternpowered.api.plugin.PluginContainer
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.api.util.collections.toImmutableMap
import org.lanternpowered.api.util.collections.toImmutableSet
import org.lanternpowered.server.catalog.AbstractCatalogBuilder
import org.lanternpowered.api.key.NamespacedKey
import org.spongepowered.api.data.DataProvider
import org.spongepowered.api.data.DataRegistration
import org.spongepowered.api.data.DuplicateProviderException
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.persistence.DataStore

class LanternDataRegistrationBuilder : AbstractCatalogBuilder<DataRegistration, DataRegistration.Builder>(), DataRegistration.Builder {

    private val keys = mutableSetOf<Key<*>>()
    private var providers = mutableMapOf<Key<*>, DataProvider<*, *>>()
    private var stores = mutableListOf<DataStore>()

    override fun store(store: DataStore) = apply {
        // TODO: Check for overlapping store targets
        this.stores.add(store)
    }

    override fun provider(provider: DataProvider<*, *>) = apply {
        val key = provider.getKey()
        if (!this.providers.containsKey(key)) throw DuplicateProviderException()
        this.providers[key] = provider
        key(key)
    }

    override fun key(key: Key<*>) = apply {
        this.keys.add(key)
    }

    override fun key(key: Key<*>, vararg others: Key<*>) = apply {
        key(key)
        key(others.asIterable())
    }

    override fun key(keys: Iterable<Key<*>>) = apply {
        keys.forEach { key(it) }
    }

    override fun reset() = apply {
        super.reset()

        this.keys.clear()
        this.providers.clear()
        this.stores.clear()
    }

    override fun build(key: NamespacedKey): DataRegistration {
        val keys = this.keys.toImmutableSet()
        check(keys.isNotEmpty()) { "At least one key must be added" }
        val pluginContainer = CauseStack.first<PluginContainer>()!!
        val providers = this.providers.toImmutableMap()
        val stores = this.stores.toImmutableList()
        return LanternDataRegistration(key, pluginContainer, keys, stores, providers)
    }
}
