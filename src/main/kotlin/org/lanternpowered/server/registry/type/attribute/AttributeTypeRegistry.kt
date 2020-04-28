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
package org.lanternpowered.server.registry.type.attribute

import org.lanternpowered.api.attribute.AttributeType
import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.registry.catalogTypeRegistry
import org.lanternpowered.server.attribute.LanternAttributeType
import org.spongepowered.api.data.DataHolder
import org.spongepowered.api.entity.living.animal.horse.HorseEntity
import org.spongepowered.api.entity.living.monster.zombie.ZombieEntity
import org.spongepowered.api.text.Text

val AttributeTypeRegistry = catalogTypeRegistry<AttributeType> {
    matchSuggestedId { suggestedId, type ->
        val value = type.key.value
        val genericPrefix = "generic."
        if (value.startsWith(genericPrefix)) {
            value.substring(genericPrefix.length) == suggestedId
        } else {
            value.replace('.', '_') == suggestedId
        }
    }

    fun register(id: String, name: String, default: Double,
                 range: ClosedFloatingPointRange<Double>, supports: (DataHolder) -> Boolean = { true }) =
            register(LanternAttributeType(CatalogKey.minecraft(id), Text.of(name), range, default, supports))

    register("generic.max_health", "Max Health", 20.0, 0.0..1024.0)
    register("generic.follow_range", "Follow Range", 32.0, 0.0..2048.0)
    register("generic.knockback_resistance", "Knockback Resistance", 0.0, 0.0..1.0)
    register("generic.attack_damage", "Attack Damage", 2.0, 0.0..2048.0)
    register("generic.attack_speed", "Attack Speed", 4.0, 0.0..1024.0)
    register("generic.attack_knockback", "Attack Knockback", 0.0, 0.0..5.0)
    register("generic.movement_speed", "Movement Speed", 0.7, 0.0..1024.0)
    register("generic.flying_speed", "Flying Speed", 0.4, 0.0..1024.0)
    register("generic.armor", "Armor", 0.0, 0.0..30.0)
    register("generic.armor_toughness", "Armor Toughness", 0.0, 0.0..20.0)
    register("generic.luck", "Luck", 0.0, -1024.0..1024.0)
    register("horse.jump_strength", "Jump Strength", 0.7, 0.0..2.0) { it is HorseEntity }
    register("zombie.spawn_reinforcements", "Spawn Reinforcements Chance", 0.0, 0.0..1.0) { it is ZombieEntity }
}
