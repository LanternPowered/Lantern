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
import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.api.namespace.resolveNamespacedKey
import org.spongepowered.api.data.type.ProfessionType

val ProfessionTypeRegistry = internalCatalogTypeRegistry<ProfessionType> {
    InternalRegistries.visit("villager_profession") { key, internalId ->
        register(internalId, LanternProfessionType(resolveNamespacedKey(key)))
    }
}

private class LanternProfessionType(key: NamespacedKey) : DefaultCatalogType(key), ProfessionType
