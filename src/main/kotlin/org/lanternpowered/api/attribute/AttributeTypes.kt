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
    val MAX_HEALTH: AttributeType by CatalogRegistry.provide("MAX_HEALTH")
    val FOLLOW_RANGE: AttributeType by CatalogRegistry.provide("FOLLOW_RANGE")
    val KNOCKBACK_RESISTANCE: AttributeType by CatalogRegistry.provide("KNOCKBACK_RESISTANCE")
    val MOVEMENT_SPEED: AttributeType by CatalogRegistry.provide("MOVEMENT_SPEED")
    val ATTACK_DAMAGE: AttributeType by CatalogRegistry.provide("ATTACK_DAMAGE")
    val ATTACK_SPEED: AttributeType by CatalogRegistry.provide("ATTACK_SPEED")
    val FLYING_SPEED: AttributeType by CatalogRegistry.provide("FLYING_SPEED")
    val ARMOR: AttributeType by CatalogRegistry.provide("ARMOR")
    val ARMOR_TOUGHNESS: AttributeType by CatalogRegistry.provide("ARMOR_TOUGHNESS")
    val LUCK: AttributeType by CatalogRegistry.provide("LUCK")
    val HORSE_JUMP_STRENGTH: AttributeType by CatalogRegistry.provide("HORSE_JUMP_STRENGTH")
    val ZOMBIE_SPAWN_REINFORCEMENTS_CHANCE: AttributeType by CatalogRegistry.provide("ZOMBIE_SPAWN_REINFORCEMENTS")
}
