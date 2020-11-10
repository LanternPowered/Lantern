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
import org.spongepowered.api.event.cause.entity.DismountType

val DismountTypeRegistry = catalogTypeRegistry<DismountType> {
    fun register(id: String) =
            register(LanternDismountType(minecraftKey(id)))

    register("death")
    register("derail")
    register("player")
}

private class LanternDismountType(key: NamespacedKey) : DefaultCatalogType(key), DismountType
