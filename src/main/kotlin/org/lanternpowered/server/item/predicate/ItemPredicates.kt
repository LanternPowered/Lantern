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
package org.lanternpowered.server.item.predicate

import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.stack.asStack
import org.lanternpowered.server.inventory.LanternItemStack

object ItemPredicates {

    /**
     * Constructs a predicate that matches
     * [ItemStack]s with the given [ItemStack].
     *
     * The provided item stack is not copied, so modifying the
     * stack will result in different predicate results.
     *
     * @param itemStack The item stack
     * @return The item predicate
     */
    @JvmStatic
    fun similarItemStack(itemStack: ItemStack): (ItemStack) -> Boolean {
        return similarItem(itemStack).asStackPredicate()
    }

    /**
     * Constructs a predicate that matches
     * [ItemStack]s with the given [ItemStack].
     *
     * @param itemStackSnapshot The item stack snapshot
     * @return The item predicate
     */
    @JvmStatic
    fun similarItemStack(itemStackSnapshot: ItemStackSnapshot): (ItemStack) -> Boolean {
        return similarItem(itemStackSnapshot).asStackPredicate()
    }

    /**
     * Constructs a [ItemPredicate] that matches
     * items with the given [ItemStack].
     *
     * The provided item stack is not copied, so modifying the
     * stack will result in different predicate results.
     *
     * @param itemStack The item stack
     * @return The item predicate
     */
    @JvmStatic
    fun similarItem(itemStack: ItemStack): ItemPredicate {
        return SimilarItemPredicate(itemStack as LanternItemStack)
    }

    /**
     * Constructs a [ItemPredicate] that matches
     * items with the given [ItemStackSnapshot].
     *
     * @param itemStackSnapshot The item stack snapshot
     * @return The item predicate
     */
    @JvmStatic
    fun similarItem(itemStackSnapshot: ItemStackSnapshot): ItemPredicate {
        return SimilarItemPredicate(itemStackSnapshot.asStack())
    }
}
