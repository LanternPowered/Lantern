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
package org.lanternpowered.server.game.registry.type.cause

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.cause.entity.dismount.LanternDismountType
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.spongepowered.api.event.cause.entity.dismount.DismountType
import org.spongepowered.api.event.cause.entity.dismount.DismountTypes

class DismountTypeRegistryModule : AdditionalPluginCatalogRegistryModule<DismountType>(DismountTypes::class) {

    override fun registerDefaults() {
        val register = { id: String -> register(LanternDismountType(CatalogKey.minecraft(id))) }
        register("death")
        register("derail")
        register("player")
    }
}
