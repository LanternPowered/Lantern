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
                            (key as LanternEventContextKey<*>).allowedType != typeToken }.orElse(true)) {
                    continue
                }
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
