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
package org.lanternpowered.server.inventory.slot

import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.ViewableInventory
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import org.lanternpowered.server.inventory.AbstractMutableInventory
import org.lanternpowered.server.inventory.AbstractSlot
import org.lanternpowered.server.inventory.InventoryView
import org.lanternpowered.server.inventory.SlotChangeTracker
import org.lanternpowered.server.inventory.TransactionConsumer

abstract class AbstractSlotView<T : AbstractSlot> : AbstractSlot(), InventoryView<T> {

    override var rawItem: ItemStack
        get() = this.backing.rawItem
        set(value) { this.backing.rawItem = value }

    override fun toViewable(): ViewableInventory? {
        TODO("Not yet implemented")
    }

    override fun addTracker(tracker: SlotChangeTracker) = this.backing.addTracker(tracker)
    override fun removeTracker(tracker: SlotChangeTracker) = this.backing.removeTracker(tracker)

    private fun TransactionConsumer.replaceSlot(): TransactionConsumer =
            { _, original, replacement -> this(this@AbstractSlotView, original, replacement) }

    override fun setAndConsume(stack: ItemStack, force: Boolean, transactionAdder: TransactionConsumer?) =
            this.backing.setAndConsume(stack, force, transactionAdder?.replaceSlot())

    override fun instantiateView(): InventoryView<AbstractMutableInventory> =
            this.backing.instantiateView()

    override fun addSlotChangeListener(listener: (ExtendedSlot) -> Unit) =
            this.backing.addSlotChangeListener(listener)

    override fun peekOfferAndConsume(stack: ItemStack, transactionAdder: TransactionConsumer?) =
            this.backing.peekOfferAndConsume(stack, transactionAdder?.replaceSlot())

    override fun offerAndConsume(stack: ItemStack, transactionAdder: TransactionConsumer?) =
            this.backing.offerAndConsume(stack, transactionAdder?.replaceSlot())

    override fun pollFast(predicate: (ItemStackSnapshot) -> Boolean): ItemStack =
            this.backing.pollFast(predicate)

    override fun pollFast(limit: Int, predicate: (ItemStackSnapshot) -> Boolean): ItemStack =
            this.backing.pollFast(limit, predicate)

    override fun peek(predicate: (ItemStackSnapshot) -> Boolean): ItemStack =
            this.backing.peek(predicate)

    override fun peek(limit: Int, predicate: (ItemStackSnapshot) -> Boolean): ItemStack =
            this.backing.peek(limit, predicate)

    override fun canContain(stack: ItemStack): Boolean =
            this.backing.canContain(stack)

    override fun contains(stack: ItemStack): Boolean =
            this.backing.contains(stack)

    override fun contains(type: ItemType): Boolean =
            this.backing.contains(type)

    override fun contains(fn: (ItemStackSnapshot) -> Boolean): Boolean =
            this.backing.contains(fn)

    override fun clear() =
            this.backing.clear()

    override fun containsAny(stack: ItemStack): Boolean =
            this.backing.containsAny(stack)

    override fun freeCapacity(): Int =
            this.backing.freeCapacity()

    override fun totalQuantity(): Int =
            this.backing.totalQuantity()

    override fun maxStackQuantityFor(stack: ItemStack): Int =
            this.backing.maxStackQuantityFor(stack)

    override fun maxStackQuantityFor(type: ItemType): Int =
            this.backing.maxStackQuantityFor(type)
}
