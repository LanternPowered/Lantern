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

import org.lanternpowered.api.item.enchantment.EnchantmentType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.text.Text
import org.lanternpowered.api.text.TextRepresentable
import org.lanternpowered.api.x.item.enchantment.XEnchantmentType
import org.lanternpowered.server.catalog.DefaultCatalogType

class LanternEnchantmentType internal constructor(
        key: NamespacedKey,
        private val name: String,
        override val levelRange: IntRange,
        private val weight: Int,
        private val treasure: Boolean,
        private val curse: Boolean,
        text: Text,
        enchantabilityRangeProvider: ((Int) -> IntRange)?,
        compatibilityTester: ((EnchantmentType) -> Boolean)?
) : DefaultCatalogType(key), XEnchantmentType, TextRepresentable by text {

    private val compatibilityTester = compatibilityTester ?: { true }
    private val enchantabilityRangeProvider = enchantabilityRangeProvider ?: {
        val min = 1 + it * 10
        val max = min + 5
        min..max
    }

    override fun getName(): String = this.name
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
