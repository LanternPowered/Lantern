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
import org.lanternpowered.server.cause.entity.damage.LanternDamageModifierType
import org.lanternpowered.server.game.registry.AdditionalPluginCatalogRegistryModule
import org.spongepowered.api.event.cause.entity.damage.DamageModifierType
import org.spongepowered.api.event.cause.entity.damage.DamageModifierTypes

class DamageModifierTypeRegistryModule : AdditionalPluginCatalogRegistryModule<DamageModifierType>(DamageModifierTypes::class) {

    override fun registerDefaults() {
        val register = { id: String -> register(LanternDamageModifierType(CatalogKey.minecraft(id))) }
        register("absorption")
        register("armor")
        register("armor_enchantment")
        register("attack_cooldown")
        register("critical_hit")
        register("defensive_potion_effect")
        register("difficulty")
        register("hard_hat")
        register("magic")
        register("negative_potion_effect")
        register("offensive_potion_effect")
        register("shield")
        register("sweaping")
        register("weapon_enchantment")
    }
}
