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
@file:JvmName("EnchantmentTypeFactory")
@file:Suppress("FunctionName", "NOTHING_TO_INLINE")

package org.lanternpowered.api.item.enchantment

import org.lanternpowered.api.key.NamespacedKey
import org.lanternpowered.api.registry.builderOf
import org.lanternpowered.api.text.translatableTextOf
import org.lanternpowered.api.x.item.enchantment.XEnchantmentType
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

typealias EnchantmentType = org.spongepowered.api.item.enchantment.EnchantmentType
typealias EnchantmentTypes = org.spongepowered.api.item.enchantment.EnchantmentTypes

/**
 * The range of the minimum and maximum level.
 */
inline val EnchantmentType.levelRange: IntRange get() = (this as XEnchantmentType).levelRange

/**
 * Gets the enchantability range for given level.
 */
fun EnchantmentType.enchantabilityRangeForLevel(level: Int): IntRange = (this as XEnchantmentType).enchantabilityRangeForLevel(level)

/**
 * Constructs a new [EnchantmentType] with the given id and the builder function.
 *
 * @param key The key of the enchantment type
 * @param fn The builder function to apply
 */
inline fun enchantmentTypeOf(key: NamespacedKey, fn: EnchantmentTypeBuilder.() -> Unit): EnchantmentType {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    return builderOf<EnchantmentTypeBuilder>().key(key).name(translatableTextOf("enchantment.${key.value}")).apply(fn).build()
}
