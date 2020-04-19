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
package org.lanternpowered.api.ext

import org.lanternpowered.api.item.enchantment.EnchantmentType
import org.lanternpowered.api.x.item.enchantment.XEnchantmentType

/**
 * The range of the minimum and maximum level.
 */
inline val EnchantmentType.levelRange: IntRange get() = (this as XEnchantmentType).levelRange

/**
 * Gets the enchantability range for given level.
 */
fun EnchantmentType.enchantabilityRangeForLevel(level: Int): IntRange = (this as XEnchantmentType).enchantabilityRangeForLevel(level)
