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

package org.lanternpowered.api.item.inventory.stack

import org.lanternpowered.api.item.inventory.ExtendedItemStack
import org.lanternpowered.api.item.inventory.ExtendedItemStackSnapshot
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.UnsafeInventoryApi
import org.lanternpowered.api.item.inventory.emptyItemStack
import kotlin.contracts.InvocationKind
import kotlin.contracts.contract
import kotlin.math.max
import kotlin.math.min

/**
 * Gets the normal item stack as an extended item stack.
 */
inline fun ItemStack.fix(): ExtendedItemStack {
    contract { returns() implies (this@fix is ExtendedItemStack) }
    return this as ExtendedItemStack
}

/**
 * Gets the normal item stack as an extended item stack.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedItemStack.fix(): ExtendedItemStack = this

/**
 * Gets the normal item stack as an extended item stack snapshot.
 */
inline fun ItemStackSnapshot.fix(): ExtendedItemStackSnapshot {
    contract { returns() implies (this@fix is ExtendedItemStackSnapshot) }
    return this as ExtendedItemStackSnapshot
}

/**
 * Gets the normal item stack as an extended item stack snapshot.
 */
@Deprecated(message = "Redundant call.", replaceWith = ReplaceWith(""))
inline fun ExtendedItemStackSnapshot.fix(): ExtendedItemStackSnapshot = this

/**
 * Gets whether this item stack is similar to the other one.
 *
 * Stacks are similar if all the data matches, excluding
 * the quantity.
 *
 * @param other The other stack to match with
 * @return Whether the stacks are similar
 */
inline infix fun ItemStack.isSimilarTo(other: ItemStack): Boolean {
    contract { returns() implies (this@isSimilarTo is ExtendedItemStack) }
    return (this as ExtendedItemStack).isSimilarTo(other)
}

/**
 * Gets whether this item stack is similar to the other one.
 *
 * Stacks are similar if all the data matches, excluding
 * the quantity.
 *
 * @param other The other stack to match with
 * @return Whether the stacks are similar
 */
inline infix fun ItemStack.isSimilarTo(other: ItemStackSnapshot): Boolean {
    contract { returns() implies (this@isSimilarTo is ExtendedItemStack) }
    return (this as ExtendedItemStack).isSimilarTo(other)
}

/**
 * Gets whether this item stack is similar to the other one.
 *
 * Stacks are similar if all the data matches, excluding
 * the quantity.
 *
 * @param other The other stack to match with
 * @return Whether the stacks are similar
 */
inline infix fun ItemStackSnapshot.isSimilarTo(other: ItemStack): Boolean {
    contract { returns() implies (this@isSimilarTo is ExtendedItemStackSnapshot) }
    return (this as ExtendedItemStackSnapshot).isSimilarTo(other)
}

/**
 * Gets whether this item stack is similar to the other one.
 *
 * Stacks are similar if all the data matches, excluding
 * the quantity.
 *
 * @param other The other stack to match with
 * @return Whether the stacks are similar
 */
inline infix fun ItemStackSnapshot.isSimilarTo(other: ItemStackSnapshot): Boolean {
    contract { returns() implies (this@isSimilarTo is ExtendedItemStackSnapshot) }
    return (this as ExtendedItemStackSnapshot).isSimilarTo(other)
}

/**
 * Gets whether this item stack is similar to the other one.
 *
 * Stacks are equal if all the data matches, including
 * the quantity.
 *
 * @param other The other stack to match with
 * @return Whether the stacks are similar
 */
inline infix fun ItemStack.isEqualTo(other: ItemStack): Boolean {
    contract { returns() implies (this@isEqualTo is ExtendedItemStack) }
    return (this as ExtendedItemStack).isEqualTo(other)
}

/**
 * Gets whether this item stack is similar to the other one.
 *
 * Stacks are equal if all the data matches, including
 * the quantity.
 *
 * @param other The other stack to match with
 * @return Whether the stacks are similar
 */
inline infix fun ItemStack.isEqualTo(other: ItemStackSnapshot): Boolean {
    contract { returns() implies (this@isEqualTo is ExtendedItemStack) }
    return (this as ExtendedItemStack).isEqualTo(other)
}

/**
 * Gets whether this item stack is similar to the other one.
 *
 * Stacks are equal if all the data matches, including
 * the quantity.
 *
 * @param other The other stack to match with
 * @return Whether the stacks are similar
 */
inline infix fun ItemStackSnapshot.isEqualTo(other: ItemStack): Boolean {
    contract { returns() implies (this@isEqualTo is ExtendedItemStackSnapshot) }
    return (this as ExtendedItemStackSnapshot).isEqualTo(other)
}

/**
 * Gets whether this item stack is similar to the other one.
 *
 * Stacks are equal if all the data matches, including
 * the quantity.
 *
 * @param other The other stack to match with
 * @return Whether the stacks are similar
 */
inline infix fun ItemStackSnapshot.isEqualTo(other: ItemStackSnapshot): Boolean {
    contract { returns() implies (this@isEqualTo is ExtendedItemStackSnapshot) }
    return (this as ExtendedItemStackSnapshot).isEqualTo(other)
}

/**
 * Creates a *view* of this [ItemStack] as an [ItemStackSnapshot], changes
 * to the item stack will reflect to the snapshot.
 *
 * This should only be used if you know what you're doing, one use case can
 * be reducing the amount of copies that are being created by conversions.
 */
@UnsafeInventoryApi
fun ItemStack.asSnapshot(): ExtendedItemStackSnapshot {
    contract { returns() implies (this@asSnapshot is ExtendedItemStack) }
    return (this as ExtendedItemStack).asSnapshot()
}

/**
 * Creates a *view* of this [ItemStackSnapshot] as an [ItemStack], changes
 * to the item stack will reflect to the snapshot.
 *
 * This should only be used if you know what you're doing, one use case can
 * be reducing the amount of copies that are being created by conversions.
 */
@UnsafeInventoryApi
fun ItemStackSnapshot.asStack(): ExtendedItemStack {
    contract { returns() implies (this@asStack is ExtendedItemStackSnapshot) }
    return (this as ExtendedItemStackSnapshot).asStack()
}

/**
 * Attempts to poll given [quantity] of items from this [ItemStack]. The
 * polled item quantity will be reduced from this stack and returned as
 * a new stack.
 *
 * @param quantity The maximum quantity to poll
 */
fun ItemStack.poll(quantity: Int): ExtendedItemStack {
    if (quantity == 0)
        return emptyItemStack()
    val copy = this.copy()
    copy.quantity = min(quantity, this.quantity)
    this.quantity = max(0, this.quantity - quantity)
    return copy.fix()
}

/**
 * Will return {@code null} if this stack is empty.
 */
fun ItemStack.orNull(): ExtendedItemStack? =
        if (this.isEmpty) null else this.fix()

/**
 * Will return the given [ItemStack] if this stack is empty.
 */
fun ItemStack.orElse(itemStack: ItemStack): ExtendedItemStack =
        if (this.isEmpty) itemStack.fix() else this.fix()

/**
 * Will return the a supplied [ItemStack] if this stack is empty.
 */
fun ItemStack.orElse(supplier: () -> ItemStack): ExtendedItemStack {
    contract {
        callsInPlace(supplier, InvocationKind.AT_MOST_ONCE)
    }
    return if (this.isEmpty) supplier().fix() else this.fix()
}

/**
 * If this item stack is null, an empty one will be returned
 * instead. Otherwise returns itself.
 */
fun ItemStack?.orEmpty(): ExtendedItemStack =
        if (this == null || this.isEmpty) emptyItemStack() else this.fix()

/**
 * Whether this item stack isn't empty.
 */
inline val ItemStack.isNotEmpty: Boolean
    get() = !this.isEmpty

/**
 * Whether this item stack isn't empty.
 */
inline val ItemStackSnapshot.isNotEmpty: Boolean
    get() = !this.isEmpty

/**
 * Executes the given function if the stack isn't empty.
 */
inline fun ItemStack.ifNotEmpty(block: (ExtendedItemStack) -> Unit) {
    contract {
        callsInPlace(block, InvocationKind.AT_MOST_ONCE)
    }
    if (!this.isEmpty)
        block(this.fix())
}
