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

package org.lanternpowered.api.item.inventory

import org.lanternpowered.api.item.inventory.stack.fix

/**
 * Gets an empty [ItemStack].
 */
inline fun emptyItemStackSnapshot(): ExtendedItemStackSnapshot =
        ItemStackSnapshot.empty().fix()

/**
 * An extended version of [ItemStackSnapshot].
 */
interface ExtendedItemStackSnapshot : ItemStackSnapshot {

    /**
     * Gets whether this item stack is similar to the other one.
     *
     * Stacks are similar if all the data matches, excluding
     * the quantity.
     *
     * @param other The other stack to match with
     * @return Whether the stacks are similar
     */
    infix fun isSimilarTo(other: ItemStack): Boolean

    /**
     * Gets whether this item stack is similar to the other one.
     *
     * Stacks are similar if all the data matches, excluding
     * the quantity.
     *
     * @param other The other stack to match with
     * @return Whether the stacks are similar
     */
    infix fun isSimilarTo(other: ItemStackSnapshot): Boolean

    /**
     * Gets whether this item stack is similar to the other one.
     *
     * Stacks are equal if all the data matches, including
     * the quantity.
     *
     * @param other The other stack to match with
     * @return Whether the stacks are similar
     */
    infix fun isEqualTo(other: ItemStack): Boolean

    /**
     * Gets whether this item stack is similar to the other one.
     *
     * Stacks are equal if all the data matches, including
     * the quantity.
     *
     * @param other The other stack to match with
     * @return Whether the stacks are similar
     */
    infix fun isEqualTo(other: ItemStackSnapshot): Boolean

    /**
     * Creates a *view* of this [ItemStackSnapshot] as an [ItemStack], changes
     * to the item stack will reflect to the snapshot.
     *
     * This should only be used if you know what you're doing, one use case can
     * be reducing the amount of copies that are being created by conversions.
     */
    @UnsafeInventoryApi
    fun asStack(): ExtendedItemStack
}
