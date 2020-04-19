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
package org.lanternpowered.api.x.item.enchantment

import org.lanternpowered.api.item.enchantment.EnchantmentType
import org.spongepowered.api.NamedCatalogType

interface XEnchantmentType : EnchantmentType, NamedCatalogType {

    /**
     * The range of the [getMinimumLevel] and [getMaximumLevel].
     */
    val levelRange: IntRange

    /**
     * Gets the enchantability range for given level.
     */
    fun enchantabilityRangeForLevel(level: Int): IntRange

    override fun getMinimumLevel() = this.levelRange.first
    override fun getMaximumLevel() = this.levelRange.last
    override fun getMinimumEnchantabilityForLevel(level: Int) = enchantabilityRangeForLevel(level).first
    override fun getMaximumEnchantabilityForLevel(level: Int) = enchantabilityRangeForLevel(level).last
}
