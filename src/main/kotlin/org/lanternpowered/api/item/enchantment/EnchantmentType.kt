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

import org.lanternpowered.api.catalog.CatalogKey
import org.lanternpowered.api.ext.*
import org.lanternpowered.server.text.translation.TranslationHelper.tr
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

typealias EnchantmentType = org.spongepowered.api.item.enchantment.EnchantmentType
typealias EnchantmentTypes = org.spongepowered.api.item.enchantment.EnchantmentTypes

/**
 * Constructs a new [EnchantmentType] with the given id and the builder function.
 *
 * @param key The key of the enchantment type
 * @param fn The builder function to apply
 */
@JvmName("of")
inline fun EnchantmentType(key: CatalogKey, fn: EnchantmentTypeBuilder.() -> Unit): EnchantmentType {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    return EnchantmentTypeBuilder().key(key).name(tr("enchantment.${key.value}")).apply(fn).build()
}

/**
 * Constructs a new [EnchantmentTypeBuilder].
 *
 * @return The constructed enchantment type builder
 */
@JvmName("builder")
inline fun EnchantmentTypeBuilder(): EnchantmentTypeBuilder = builderOf()
