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

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.util.optional.emptyOptional
import org.lanternpowered.api.util.uncheckedCast
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.data.DataProvider
import org.spongepowered.api.data.DataRegistration
import org.spongepowered.api.data.Key
import org.spongepowered.api.data.UnregisteredKeyException
import org.spongepowered.api.data.persistence.DataStore
import org.spongepowered.api.data.value.Value
import org.spongepowered.api.plugin.PluginContainer
import java.util.Optional

class LanternDataRegistration(
        key: CatalogKey,
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
