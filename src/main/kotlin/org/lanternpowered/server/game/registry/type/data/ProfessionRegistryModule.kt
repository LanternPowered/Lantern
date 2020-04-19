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

import org.lanternpowered.server.data.type.LanternProfession
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule
import org.lanternpowered.server.game.registry.InternalRegistries
import org.spongepowered.api.CatalogKey
import org.spongepowered.api.data.type.Profession
import org.spongepowered.api.data.type.Professions

class ProfessionRegistryModule : DefaultCatalogRegistryModule<Profession>(Professions::class) {

    override fun registerDefaults() {
        InternalRegistries.visit("villager_profession") { key, internalId ->
            register(LanternProfession(CatalogKey.resolve(key), internalId))
        }
    }
}
