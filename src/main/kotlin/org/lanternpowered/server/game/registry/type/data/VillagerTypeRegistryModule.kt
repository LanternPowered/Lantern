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
package org.lanternpowered.server.game.registry.type.data

import org.lanternpowered.server.data.type.LanternVillagerType
import org.lanternpowered.server.game.registry.InternalPluginCatalogRegistryModule
import org.lanternpowered.server.game.registry.InternalRegistries
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.data.type.VillagerType
import org.spongepowered.api.data.type.VillagerTypes

class VillagerTypeRegistryModule : InternalPluginCatalogRegistryModule<VillagerType>(VillagerTypes::class) {

    override fun registerDefaults() {
        InternalRegistries.visit("villager_type") { key, internalId ->
            register(LanternVillagerType(CatalogKey.resolve(key), internalId))
        }
    }
}
