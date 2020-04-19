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

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.cause.entity.teleport.LanternTeleportType
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.spongepowered.api.event.cause.entity.teleport.TeleportType
import org.spongepowered.api.event.cause.entity.teleport.TeleportTypes

class TeleportTypeRegistryModule : AdditionalPluginCatalogRegistryModule<TeleportType>(TeleportTypes::class) {

    override fun registerDefaults() {
        val register = { id: String -> register(LanternTeleportType(CatalogKey.minecraft(id))) }
        register("command")
        register("entity_teleport")
        register("plugin")
        register("portal")
        register("unknown")
    }
}
