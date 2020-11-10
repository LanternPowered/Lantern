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

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.key.minecraftKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.spongepowered.api.event.cause.entity.MovementType

val MovementTypeRegistry = catalogTypeRegistry<MovementType> {
    fun register(id: String) =
            register(LanternTeleportType(minecraftKey(id)))

    register("chorus_fruit")
    register("command")
    register("end_gateway")
    register("ender_pearl")
    register("entity_teleport")
    register("natural")
    register("plugin")
    register("portal")
}

private class LanternTeleportType(key: NamespacedKey) : DefaultCatalogType(key), MovementType
