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

typealias ItemStack = org.spongepowered.api.item.inventory.ItemStack
typealias ItemStackSnapshot = org.spongepowered.api.item.inventory.ItemStackSnapshot
typealias ItemStackBuilder = org.spongepowered.api.item.inventory.ItemStack.Builder

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
inline fun ItemStack.ifNotEmpty(block: (ItemStack) -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    if (!isEmpty) block(this)
}

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

interface ExtendedItemStack : ItemStack {

    fun isSimilarTo(other: ItemStack): Boolean

    fun isSimilarTo(other: ItemStackSnapshot): Boolean
}

interface ExtendedItemStackSnapshot : ItemStackSnapshot {

    fun isSimilarTo(other: ItemStack): Boolean

    fun isSimilarTo(other: ItemStackSnapshot): Boolean
}
