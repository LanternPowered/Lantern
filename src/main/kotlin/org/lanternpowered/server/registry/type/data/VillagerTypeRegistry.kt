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

import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.game.registry.InternalRegistries
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.data.type.VillagerType

val VillagerTypeRegistry = internalCatalogTypeRegistry<VillagerType> {
    InternalRegistries.visit("villager_type") { key, internalId ->
        register(internalId, LanternVillagerType(CatalogKey.resolve(key)))
    }
}

private class LanternVillagerType(key: CatalogKey) : DefaultCatalogType(key), VillagerType
