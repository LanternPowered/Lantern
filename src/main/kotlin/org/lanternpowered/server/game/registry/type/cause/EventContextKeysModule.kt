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
package org.lanternpowered.server.game.registry.type.cause

import com.google.common.reflect.TypeToken
import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.catalog.CatalogKeys
import org.lanternpowered.server.behavior.ContextKeys
import org.lanternpowered.server.event.LanternEventContextKey
import org.lanternpowered.server.event.LanternEventContextKeys
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.spongepowered.api.event.cause.EventContextKey
import org.spongepowered.api.event.cause.EventContextKeys
import kotlin.reflect.KClass

object EventContextKeysModule : AdditionalPluginCatalogRegistryModule<EventContextKey<*>>(
        EventContextKeys::class, LanternEventContextKeys::class, ContextKeys::class) {

    @JvmStatic
    fun get(): EventContextKeysModule = this

    override fun registerDefaults() {
        val registerKeys = { pluginId: String, catalogClass: KClass<*> ->
            val typeVariable = EventContextKey::class.java.typeParameters[0]
            // Sponge
            for (field in catalogClass.java.fields) {
                // Skip fields that aren't event context keys
                if (!EventContextKey::class.java.isAssignableFrom(field.type)) {
                    break
                }
                // Extract the generic type from the field signature
                val typeToken = TypeToken.of(field.genericType).resolveType(typeVariable)
                // Get the plugin id, and make a nicely formatted name
                val id = field.name.toLowerCase()
                // Filter duplicates
                if (!get(CatalogKey(pluginId, id)).map { key: EventContextKey<*> ->
                            (key as LanternEventContextKey<*>).allowedTypeToken != typeToken }.orElse(true)) {
                    continue
                }
                // val name = id.split("_").joinToString(separator = " ") { s -> s[0].toUpperCase() + s.substring(1) }
                register(LanternEventContextKey(CatalogKey(pluginId, id), typeToken))
            }
        }

        // Sponge
        registerKeys(CatalogKeys.SPONGE_NAMESPACE, EventContextKeys::class)
        // Lantern
        registerKeys(CatalogKeys.LANTERN_NAMESPACE, LanternEventContextKeys::class)
        registerKeys(CatalogKeys.LANTERN_NAMESPACE, ContextKeys::class)
    }
}
