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
package org.lanternpowered.api.attribute

import org.lanternpowered.api.registry.CatalogRegistry
import org.lanternpowered.api.registry.provide

object AttributeTypes {
    val MAX_HEALTH: AttributeType = CatalogRegistry.provide("MAX_HEALTH")
    val FOLLOW_RANGE: AttributeType = CatalogRegistry.provide("FOLLOW_RANGE")
    val KNOCKBACK_RESISTANCE: AttributeType = CatalogRegistry.provide("KNOCKBACK_RESISTANCE")
    val MOVEMENT_SPEED: AttributeType = CatalogRegistry.provide("MOVEMENT_SPEED")
    val ATTACK_DAMAGE: AttributeType = CatalogRegistry.provide("ATTACK_DAMAGE")
    val ATTACK_SPEED: AttributeType = CatalogRegistry.provide("ATTACK_SPEED")
    val FLYING_SPEED: AttributeType = CatalogRegistry.provide("FLYING_SPEED")
    val ARMOR: AttributeType = CatalogRegistry.provide("ARMOR")
    val ARMOR_TOUGHNESS: AttributeType = CatalogRegistry.provide("ARMOR_TOUGHNESS")
    val LUCK: AttributeType = CatalogRegistry.provide("LUCK")
    val HORSE_JUMP_STRENGTH: AttributeType = CatalogRegistry.provide("HORSE_JUMP_STRENGTH")
    val ZOMBIE_SPAWN_REINFORCEMENTS_CHANCE: AttributeType = CatalogRegistry.provide("ZOMBIE_SPAWN_REINFORCEMENTS")
}
