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
import org.lanternpowered.api.key.resolveNamespacedKey
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.game.registry.InternalRegistries
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.spongepowered.api.data.type.CatType

val CatTypeRegistry = internalCatalogTypeRegistry<CatType> {
    InternalRegistries.visit("cat_type") { key, internalId ->
        register(internalId, LanternCatType(resolveNamespacedKey(key)))
    }
}

private class LanternCatType(key: NamespacedKey) : DefaultCatalogType(key), CatType
