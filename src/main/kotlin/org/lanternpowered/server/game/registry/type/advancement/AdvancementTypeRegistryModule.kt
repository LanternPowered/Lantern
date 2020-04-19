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
package org.lanternpowered.server.game.registry.type.advancement

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.server.advancement.LanternAdvancementType
import org.lanternpowered.server.game.registry.DefaultCatalogRegistryModule
import org.spongepowered.api.advancement.AdvancementType
import org.spongepowered.api.advancement.AdvancementTypes
import org.spongepowered.api.text.format.TextColor
import org.spongepowered.api.text.format.TextColors
import org.spongepowered.api.text.format.TextFormat

class AdvancementTypeRegistryModule : DefaultCatalogRegistryModule<AdvancementType>(AdvancementTypes::class) {

    override fun registerDefaults() {
        val register = { id: String, internalId: Int, color: TextColor ->
            register(LanternAdvancementType(CatalogKey.minecraft(id), internalId, TextFormat.of(color))) }
        register("task", 0, TextColors.YELLOW)
        register("challenge", 1, TextColors.LIGHT_PURPLE)
        register("goal", 2, TextColors.YELLOW)
    }
}
