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

import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.inventory.ExtendedItemStack
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.itemStackOf

/**
 * A [ItemPredicate] that tests whether the given
 * [ItemStack] is similar to the target stack.
 */
internal class SimilarItemPredicate(private val itemStack: ExtendedItemStack) : ItemPredicate {

    override fun invoke(stack: ItemStack) = this.itemStack.isSimilarTo(stack)
    override fun invoke(type: ItemType) = this.itemStack.isSimilarTo(itemStackOf(type))
    override fun invoke(stack: ItemStackSnapshot) = this.itemStack.isSimilarTo(stack)
}
