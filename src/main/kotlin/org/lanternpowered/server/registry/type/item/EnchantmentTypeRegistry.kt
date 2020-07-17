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
@file:JvmName("EnchantmentTypeRegistry")
package org.lanternpowered.server.registry.type.item

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.api.item.enchantment.EnchantmentType
import org.lanternpowered.api.item.enchantment.EnchantmentTypeBuilder
import org.lanternpowered.server.item.enchantment.LanternEnchantmentTypeBuilder
import org.lanternpowered.server.registry.internalCatalogTypeRegistry
import org.lanternpowered.server.text.translation.TranslationHelper
import org.spongepowered.api.item.enchantment.EnchantmentTypes

@get:JvmName("get")
val EnchantmentTypeRegistry = internalCatalogTypeRegistry<EnchantmentType> {
    fun register(key: ResourceKey, name: String, fn: EnchantmentTypeBuilder.() -> Unit = {}) =
            register(LanternEnchantmentTypeBuilder().key(key).name(TranslationHelper.tr(name)).apply(fn).build())

    fun register(id: String, name: String, fn: EnchantmentTypeBuilder.() -> Unit = {}) =
            register(ResourceKey.minecraft(id), name, fn)

    register("protection", "enchantment.protect.all")
    register("fire_protection", "enchantment.protect.fire")
    register("feather_falling", "enchantment.protect.fall")
    register("blast_protection", "enchantment.protect.explosion")
    register("projectile_protection", "enchantment.protect.projectile")
    register("respiration", "enchantment.oxygen")
    register("aqua_affinity", "enchantment.waterWorker")
    register("thorns", "enchantment.thorns")
    register("depth_strider", "enchantment.waterWalker")
    register("frost_walker", "enchantment.frostWalker")
    register("sharpness", "enchantment.damage.all")
    register("smite", "enchantment.damage.undead")
    register("bane_of_arthropods", "enchantment.damage.arthropods")
    register("knockback", "enchantment.knockback") {
        maxLevel(2)
        enchantabilityRange {
            val min = 5 + (it - 1) * 20
            val max = min + 50
            min..max
        }
    }
    register("fire_aspect", "enchantment.fire") {
        maxLevel(2)
        enchantabilityRange {
            val min = 10 + (it - 1) * 20
            val max = min + 50
            min..max
        }
    }
    val lootBonusModifier: EnchantmentTypeBuilder.() -> Unit = {
        enchantabilityRange {
            val min = 15 + (it - 1) * 9
            val max = min + 50
            min..max
        }
        maxLevel(3)
        compatibilityTester { it != EnchantmentTypes.SILK_TOUCH.get() }
    }
    register("looting", "enchantment.lootBonus") {
        apply(lootBonusModifier)
    }
    register("sweeping", "enchantment.sweeping") {
        maxLevel(3)
        enchantabilityRange {
            val min = 5 + (it - 1) * 9
            val max = min + 15
            min..max
        }
    }
    register("efficiency", "enchantment.digging")
    register("silk_touch", "enchantment.untouching")
    register("unbreaking", "enchantment.durability") {
        maxLevel(3)
        enchantabilityRange {
            val min = 5 + (it - 1) * 8
            val max = min + 50
            min..max
        }
    }
    register("fortune", "enchantment.lootBonusDigger") {
        apply(lootBonusModifier)
    }
    register("power", "enchantment.arrowDamage")
    register("punch", "enchantment.arrowKnockback")
    register("flame", "enchantment.arrowFire")
    register("infinity", "enchantment.arrowInfinite")
    register("luck_of_the_sea", "enchantment.lootBonusFishing") {
        apply(lootBonusModifier)
    }
    register("lure", "enchantment.fishingSpeed")
    register("mending", "enchantment.mending")
    register("vanishing_curse", "enchantment.vanishing_curse")
}
