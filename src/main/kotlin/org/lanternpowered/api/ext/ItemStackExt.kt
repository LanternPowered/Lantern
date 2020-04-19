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
@file:Suppress("NOTHING_TO_INLINE")

package org.lanternpowered.api.ext

import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.ItemStackBuilder
import org.spongepowered.api.item.ItemType
import java.util.function.Supplier
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract

/**
 * Will return {@code null} if this stack is empty.
 */
fun ItemStack.orNull(): ItemStack? = if (isEmpty) null else this

/**
 * Will return the given [ItemStack] if this stack is empty.
 */
fun ItemStack.orElse(itemStack: ItemStack): ItemStack = if (isEmpty) itemStack else this

/**
 * Will return the a supplied [ItemStack] if this stack is empty.
 */
fun ItemStack.orElse(supplier: () -> ItemStack): ItemStack {
    contract {
        callsInPlace(supplier, InvocationKind.AT_MOST_ONCE)
    }
    return if (isEmpty) supplier() else this
}

/**
 * Whether this item stack isn't empty.
 */
inline val ItemStack.isNotEmpty get() = !this.isEmpty

/**
 * Executes the given function if the stack isn't empty.
 */
inline fun ItemStack.ifNotEmpty(fn: (ItemStack) -> Unit) {
    contract {
        callsInPlace(fn, InvocationKind.AT_MOST_ONCE)
    }
    if (!isEmpty) fn(this)
}

/**
 * Constructs a new [ItemStack] with the given [ItemType], quantity and
 * possibility to apply other data using the function.
 */
inline fun itemStackOf(type: Supplier<out ItemType>, quantity: Int = 1, fn: ItemStackBuilder.() -> Unit = {}): ItemStack {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    return itemStackOf(type.get(), quantity, fn)
}

/**
 * Constructs a new [ItemStack] with the given [ItemType], quantity and
 * possibility to apply other data using the function.
 */
inline fun itemStackOf(type: ItemType, quantity: Int = 1, fn: ItemStackBuilder.() -> Unit = {}): ItemStack {
    contract {
        callsInPlace(fn, InvocationKind.EXACTLY_ONCE)
    }
    return ItemStack.builder().itemType(type).quantity(quantity).apply(fn).build()
}
