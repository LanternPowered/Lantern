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
package org.lanternpowered.server.registry.type.data

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.event.cause.entity.teleport.TeleportType

val TeleportTypeRegistry = catalogTypeRegistry<TeleportType> {
    fun register(id: String) =
            register(LanternTeleportType(ResourceKey.minecraft(id)))

    register("command")
    register("entity_teleport")
    register("plugin")
    register("portal")
    register("unknown")
}

private class LanternTeleportType(key: ResourceKey) : DefaultCatalogType(key), TeleportType
