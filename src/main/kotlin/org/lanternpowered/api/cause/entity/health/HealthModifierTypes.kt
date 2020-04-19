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
import org.lanternpowered.api.registry.provideSupplier
import org.spongepowered.api.effect.potion.PotionEffect
import org.spongepowered.api.effect.potion.PotionEffectTypes
import org.spongepowered.api.entity.Entity
import org.spongepowered.api.item.enchantment.Enchantment
import org.spongepowered.api.item.enchantment.EnchantmentType
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot
import org.spongepowered.api.world.World
import org.spongepowered.api.world.difficulty.Difficulty
import java.util.function.Supplier

object HealthModifierTypes {

    /**
     * Represents a [HealthModifier] that "absorbs" damage based on
     * the [PotionEffectTypes.ABSORPTION] level on the
     * [Entity].
     */
    @JvmField val ABSORPTION: Supplier<HealthModifierType> = CatalogRegistry.provideSupplier("ABSORPTION")

    /**
     * Represents a [HealthModifier] that will reduce damage based on
     * the armor [ItemStack]s.
     */
    @JvmField val ARMOR: Supplier<HealthModifierType> = CatalogRegistry.provideSupplier("ARMOR")

    /**
     * Represents a [HealthModifier] that will modify the heal amount
     * from a [PotionEffect] affecting the target.
     */
    @JvmField val DEFENSIVE_POTION_EFFECT: Supplier<HealthModifierType> = CatalogRegistry.provideSupplier("DEFENSIVE_POTION_EFFECT")

    /**
     * Represents a [HealthModifier] that enhances damage based on the
     * current [Difficulty] of the [World].
     */
    @JvmField val DIFFICULTY: Supplier<HealthModifierType> = CatalogRegistry.provideSupplier("DIFFICULTY")

    /**
     * Represents a [HealthModifier] that will modify damage based on
     * magic.
     */
    @JvmField val MAGIC: Supplier<HealthModifierType> = CatalogRegistry.provideSupplier("MAGIC")

    /**
     * Represents the [HealthModifier] that will increase heal amount
     * from a [PotionEffect] affecting the target.
     */
    @JvmField val OFFENSIVE_POTION_EFFECT: Supplier<HealthModifierType> = CatalogRegistry.provideSupplier("OFFENSIVE_POTION_EFFECT")

    /**
     * Represents the [HealthModifier] that will modify heal amount from
     * an [EnchantmentType] on an equipped [ItemStack].
     *
     * Usually, within the [HealthModifier.getCause] will reside
     * an [ItemStackSnapshot] and an [Enchantment] signifying
     * that the [EnchantmentType] of the [ItemStack] is modifying the
     * incoming/outgoing heal amount.
     */
    @JvmField val WEAPON_ENCHANTMENT: Supplier<HealthModifierType> = CatalogRegistry.provideSupplier("WEAPON_ENCHANTMENT")
}
