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

import org.lanternpowered.api.ext.itemStackOf
import org.lanternpowered.server.inventory.LanternItemStack
import org.spongepowered.api.item.ItemType
import org.spongepowered.api.item.inventory.ItemStack
import org.spongepowered.api.item.inventory.ItemStackSnapshot

/**
 * A [ItemPredicate] that tests whether the given
 * [ItemStack] is similar to the target stack.
 */
internal class SimilarItemPredicate(private val itemStack: LanternItemStack) : ItemPredicate {

    override fun test(stack: ItemStack) = this.itemStack.isSimilarTo(stack)
    override fun test(type: ItemType) = this.itemStack.isSimilarTo(itemStackOf(type))
    override fun test(stack: ItemStackSnapshot) = this.itemStack.isSimilarTo(stack)
}
