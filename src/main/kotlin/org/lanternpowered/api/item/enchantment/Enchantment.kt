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
@file:Suppress("FunctionName", "NOTHING_TO_INLINE")

package org.lanternpowered.api.item.enchantment

import java.util.function.Supplier

typealias Enchantment = org.spongepowered.api.item.enchantment.Enchantment

/**
 * Constructs a new [Enchantment] with the given [EnchantmentType] and level. If no
 * level is specified, then will the minimum one be used instead.
 *
 * @param type The enchantment type
 * @param level The level of the enchantment
 * @return The constructed enchantment
 */
inline fun enchantmentOf(type: Supplier<out EnchantmentType>, level: Int = type.get().minimumLevel): Enchantment = Enchantment.of(type, level)

/**
 * Constructs a new [Enchantment] with the given [EnchantmentType] and level. If no
 * level is specified, then will the minimum one be used instead.
 *
 * @param type The enchantment type
 * @param level The level of the enchantment
 * @return The constructed enchantment
 */
inline fun enchantmentOf(type: EnchantmentType, level: Int = type.minimumLevel): Enchantment = Enchantment.of(type, level)
