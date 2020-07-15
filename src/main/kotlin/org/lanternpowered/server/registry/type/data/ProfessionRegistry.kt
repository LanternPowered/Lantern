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
import org.spongepowered.api.ResourceKey
import org.spongepowered.api.data.type.Profession

val ProfessionRegistry = internalCatalogTypeRegistry<Profession> {
    InternalRegistries.visit("villager_profession") { key, internalId ->
        register(internalId, LanternProfession(ResourceKey.resolve(key)))
    }
}

private class LanternProfession(key: ResourceKey) : DefaultCatalogType(key), Profession
