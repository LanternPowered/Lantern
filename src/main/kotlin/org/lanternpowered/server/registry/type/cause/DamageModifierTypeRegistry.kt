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
package org.lanternpowered.server.registry.type.cause

import org.lanternpowered.api.cause.entity.damage.DamageModifierType
import org.lanternpowered.api.namespace.minecraftKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.cause.entity.damage.LanternDamageModifierType

val DamageModifierTypeRegistry = catalogTypeRegistry<DamageModifierType> {
    fun register(id: String) =
            register(LanternDamageModifierType(minecraftKey(id)))

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
