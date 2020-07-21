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
package org.lanternpowered.api.cause.entity.health

import org.lanternpowered.api.registry.CatalogRegistry
import org.lanternpowered.api.registry.provide
import org.spongepowered.api.effect.potion.PotionEffect
import org.spongepowered.api.effect.potion.PotionEffectTypes
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.item.enchantment.Enchantment
import org.spongepowered.api.item.enchantment.EnchantmentType
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.world.World
import org.spongepowered.api.world.difficulty.Difficulty

object HealthModifierTypes {

    /**
     * Represents a [HealthModifier] that "absorbs" damage based on
     * the [PotionEffectTypes.ABSORPTION] level on the
     * [Entity].
     */
    val ABSORPTION: HealthModifierType by CatalogRegistry.provide("ABSORPTION")

    /**
     * Represents a [HealthModifier] that will reduce damage based on
     * the armor [ItemStack]s.
     */
    val ARMOR: HealthModifierType by CatalogRegistry.provide("ARMOR")

    /**
     * Represents a [HealthModifier] that will modify the heal amount
     * from a [PotionEffect] affecting the target.
     */
    val DEFENSIVE_POTION_EFFECT: HealthModifierType by CatalogRegistry.provide("DEFENSIVE_POTION_EFFECT")

    /**
     * Represents a [HealthModifier] that enhances damage based on the
     * current [Difficulty] of the [World].
     */
    val DIFFICULTY: HealthModifierType by CatalogRegistry.provide("DIFFICULTY")

    /**
     * Represents a [HealthModifier] that will modify damage based on
     * magic.
     */
    val MAGIC: HealthModifierType by CatalogRegistry.provide("MAGIC")

    /**
     * Represents the [HealthModifier] that will increase heal amount
     * from a [PotionEffect] affecting the target.
     */
    val OFFENSIVE_POTION_EFFECT: HealthModifierType by CatalogRegistry.provide("OFFENSIVE_POTION_EFFECT")

    /**
     * Represents the [HealthModifier] that will modify heal amount from
     * an [EnchantmentType] on an equipped [ItemStack].
     *
     * Usually, within the [HealthModifier.getCause] will reside
     * an [ItemStackSnapshot] and an [Enchantment] signifying
     * that the [EnchantmentType] of the [ItemStack] is modifying the
     * incoming/outgoing heal amount.
     */
    val WEAPON_ENCHANTMENT: HealthModifierType by CatalogRegistry.provide("WEAPON_ENCHANTMENT")
}
