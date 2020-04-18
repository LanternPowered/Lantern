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
package org.lanternpowered.server.data

import org.lanternpowered.api.cause.CauseStack
import org.lanternpowered.api.cause.first
import org.lanternpowered.api.plugin.PluginContainer
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.api.util.collections.toImmutableMap
import org.lanternpowered.api.util.collections.toImmutableSet
import org.lanternpowered.server.catalog.AbstractCatalogBuilder
import org.spongepowered.api.CatalogKey
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

    override fun build(key: CatalogKey): DataRegistration {
        val keys = this.keys.toImmutableSet()
        check(keys.isNotEmpty()) { "At least one key must be added" }
        val pluginContainer = CauseStack.current().first<PluginContainer>()!!
        val providers = this.providers.toImmutableMap()
        val stores = this.stores.toImmutableList()
        return LanternDataRegistration(key, pluginContainer, keys, stores, providers)
    }
}
