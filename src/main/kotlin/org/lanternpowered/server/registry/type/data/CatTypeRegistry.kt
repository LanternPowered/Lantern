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

import org.lanternpowered.server.data.type.LanternCatType
import org.lanternpowered.server.game.registry.InternalRegistries
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.data.type.CatType

val CatTypeRegistry = internalCatalogTypeRegistry<CatType> {
    InternalRegistries.visit("cat_type") { key, internalId ->
        register(internalId, LanternCatType(CatalogKey.resolve(key)))
    }
}
