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

package org.lanternpowered.api.item.inventory

import org.lanternpowered.api.item.ItemType
import java.util.function.Supplier
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

typealias ItemStackSnapshot = org.spongepowered.api.item.inventory.ItemStackSnapshot
typealias ItemStackBuilder = org.spongepowered.api.item.inventory.ItemStack.Builder

/**
 * Constructs a new [ItemStack] with the given [ItemType], quantity and
 * possibility to apply other data using the function.
 */
inline fun itemStackOf(type: Supplier<out ItemType>, quantity: Int = 1, block: ItemStackBuilder.() -> Unit = {}): ItemStack {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return itemStackOf(type.get(), quantity, block)
}

/**
 * Constructs a new [ItemStack] with the given [ItemType], quantity and
 * possibility to apply other data using the function.
 */
inline fun itemStackOf(type: ItemType, quantity: Int = 1, block: ItemStackBuilder.() -> Unit = {}): ItemStack {
    contract {
        callsInPlace(block, InvocationKind.EXACTLY_ONCE)
    }
    return ItemStack.builder().itemType(type).quantity(quantity).apply(block).build()
}
