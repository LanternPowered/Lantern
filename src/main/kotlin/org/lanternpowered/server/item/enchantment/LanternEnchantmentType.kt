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
package org.lanternpowered.server.item.enchantment

import org.lanternpowered.api.ResourceKey
import org.lanternpowered.api.x.item.enchantment.XEnchantmentType
import org.lanternpowered.server.catalog.DefaultCatalogType
import org.lanternpowered.server.catalog.InternalCatalogType
import org.lanternpowered.server.text.translation.Translated
import org.spongepowered.api.item.enchantment.EnchantmentType
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.text.translation.Translatable
import org.spongepowered.api.text.translation.Translation

class LanternEnchantmentType internal constructor(
        key: ResourceKey, translation: Translation, override val internalId: Int,
        override val levelRange: IntRange,
        private val weight: Int,
        private val treasure: Boolean,
        private val curse: Boolean,
        enchantabilityRangeProvider: ((Int) -> IntRange)?,
        compatibilityTester: ((EnchantmentType) -> Boolean)?
) : DefaultCatalogType(key), XEnchantmentType, InternalCatalogType, Translatable by Translated(translation) {

    private val compatibilityTester = compatibilityTester ?: { true }
    private val enchantabilityRangeProvider = enchantabilityRangeProvider ?: {
        val min = 1 + it * 10
        val max = min + 5
        min..max
    }

    override fun getName(): String = this.translation.get()
    override fun getWeight(): Int = this.weight
    override fun isTreasure(): Boolean = this.treasure
    override fun isCurse(): Boolean = this.curse
    override fun enchantabilityRangeForLevel(level: Int): IntRange = this.enchantabilityRangeProvider(level)

    override fun canBeAppliedToStack(stack: ItemStack): Boolean = canBeAppliedByTable(stack)
    override fun canBeAppliedByTable(stack: ItemStack): Boolean {
        return false
    }

    override fun isCompatibleWith(ench: EnchantmentType): Boolean {
        if (ench == this) {
            return false
        }
        return this.compatibilityTester(ench) &&
                (ench as LanternEnchantmentType).compatibilityTester(this)
    }
}
