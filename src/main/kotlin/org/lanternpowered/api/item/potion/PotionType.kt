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

package org.lanternpowered.api.item.potion

import org.lanternpowered.api.namespace.NamespacedKey
import org.lanternpowered.api.registry.builderOf
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

typealias PotionType = org.spongepowered.api.item.potion.PotionType
typealias PotionTypes = org.spongepowered.api.item.potion.PotionTypes

/**
 * Constructs a new [PotionType] with the given [NamespacedKey] and
 * possibility to apply other data using the function.
 */
inline fun potionTypeOf(key: NamespacedKey, fn: PotionTypeBuilder.() -> Unit = {}): PotionType {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    return builderOf<PotionTypeBuilder>().key(key).apply(fn).build()
}
