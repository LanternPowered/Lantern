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

import org.lanternpowered.server.data.type.LanternCatType
import org.lanternpowered.server.game.registry.InternalPluginCatalogRegistryModule
import org.lanternpowered.server.game.registry.InternalRegistries
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.data.type.CatType
import org.spongepowered.api.data.type.CatTypes

class CatTypeRegistryModule : InternalPluginCatalogRegistryModule<CatType>(CatTypes::class) {

    override fun registerDefaults() {
        InternalRegistries.visit("cat_type") { key, internalId ->
            register(LanternCatType(CatalogKey.resolve(key), internalId))
        }
    }
}
