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

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.util.optional.optional
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataProvider
import org.spongepowered.api.data.DataRegistration
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.UnregisteredKeyException
import org.spongepowered.api.data.persistence.DataStore
import org.spongepowered.api.data.value.Value
import org.spongepowered.plugin.PluginContainer
import java.util.Optional

class LanternDataRegistration(
        key: ResourceKey,
        private val pluginContainer: PluginContainer,
        private val keys: Set<Key<*>>,
        private val dataStores: List<DataStore>,
        private val providers: Map<Key<*>, DataProvider<*,*>>
) : DefaultCatalogType(key), DataRegistration {

    override fun <V : Value<E>, E : Any> getProviderFor(key: Key<V>): Optional<DataProvider<V, E>> {
        val provider = this.providers[key]
        if (provider != null) {
            return provider.uncheckedCast<DataProvider<V, E>>().optional()
        }
        if (key !in this.keys) throw UnregisteredKeyException()
        return emptyOptional()
    }

    override fun getDataStore(token: TypeToken<out DataHolder>) =
            this.dataStores.first { store -> token.isSubtypeOf(store.supportedToken) }.optional()

    override fun getKeys() = this.keys
    override fun getPluginContainer() = this.pluginContainer

    override fun toStringHelper() = super.toStringHelper()
            .add("keys", this.keys.map { it.key }.joinToString(separator = ",", prefix = "[", postfix = "]"))
}
