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

import org.lanternpowered.api.ext.*
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

typealias Enchantment = org.spongepowered.api.item.enchantment.Enchantment
typealias EnchantmentBuilder = org.spongepowered.api.item.enchantment.Enchantment.Builder

/**
 * Constructs a new [Enchantment] with the given [EnchantmentType] and level. If no
 * level is specified, then will the minimum one be used instead.
 *
 * @param type The enchantment type
 * @param level The level of the enchantment
 * @return The constructed enchantment
 */
inline fun Enchantment(type: EnchantmentType, level: Int = type.minimumLevel, fn: EnchantmentBuilder.() -> Unit = {}): Enchantment {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    return EnchantmentBuilder().type(type).level(level).apply(fn).build()
}

/**
 * Constructs a new [EnchantmentBuilder].
 *
 * @return The constructed enchantment builder
 */
inline fun EnchantmentBuilder(): EnchantmentBuilder = builderOf()
