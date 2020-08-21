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
import org.lanternpowered.api.item.inventory.emptyItemStack
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import org.lanternpowered.api.item.inventory.stack.asSnapshot
import org.lanternpowered.api.item.inventory.stack.isEqualTo
import org.lanternpowered.api.item.inventory.stack.isNotEmpty
import org.lanternpowered.api.item.inventory.stack.isSimilarTo
import org.lanternpowered.server.inventory.AbstractSlot
import org.lanternpowered.server.inventory.InventoryView
import org.lanternpowered.server.inventory.SlotChangeTracker
import org.lanternpowered.server.inventory.TransactionConsumer
import org.lanternpowered.server.item.predicate.ItemPredicate
import org.spongepowered.api.item.inventory.type.ViewableInventory
import java.util.HashSet
import kotlin.math.min

open class LanternSlot : AbstractSlot() {

    private var maxStackQuantity: Int = 64

    override fun maxStackQuantityFor(stack: ItemStack): Int =
            min(stack.maxStackQuantity, this.maxStackQuantity)

    override var rawItem: ItemStack = emptyItemStack()

    /**
     * All the [SlotChangeTracker]s that track this slot.
     */
    private val trackers = HashSet<SlotChangeTracker>()

    /**
     * Slot change listeners may track slot changes, these listeners
     * have to be removed manually after they are no longer needed.
     */
    private val changeListeners = ArrayList<(ExtendedSlot) -> Unit>()

    /**
     * The filter applied to this slot.
     */
    var filter: ItemPredicate? = null

    override fun addTracker(tracker: SlotChangeTracker) {
        this.trackers += tracker
    }

    override fun removeTracker(tracker: SlotChangeTracker) {
        this.trackers -= tracker
    }

    override fun setAndConsume(stack: ItemStack, force: Boolean, transactionAdder: TransactionConsumer?) {
        if (stack.isNotEmpty && !force && !this.canContain(stack))
            return
        if (stack.isEqualTo(this.rawItem))
            return
        val originalSnapshot = if (transactionAdder == null) null else this.rawItem.createSnapshot()
        if (stack.isEmpty) {
            this.rawItem = emptyItemStack()
        } else {
            val max = this.maxStackQuantityFor(stack)
            val set =  min(stack.quantity, max)
            this.rawItem = stack.copy()
            this.rawItem.quantity = set
            stack.quantity -= set
        }
        if (transactionAdder != null) {
            val resultSnapshot = this.rawItem.createSnapshot()
            transactionAdder(this, originalSnapshot!!, resultSnapshot)
        }
        this.queueUpdate()
    }

    override fun addSlotChangeListener(listener: (ExtendedSlot) -> Unit) {
        this.changeListeners += listener
    }

    override fun toViewable(): ViewableInventory? {
        TODO("Not yet implemented")
    }

    override fun peekOfferAndConsume(stack: ItemStack, transactionAdder: TransactionConsumer?) =
            this.offerAndConsume(stack, transactionAdder, peek = true)

    override fun offerAndConsume(stack: ItemStack, transactionAdder: TransactionConsumer?) =
            this.offerAndConsume(stack, transactionAdder, peek = false)

    private fun offerAndConsume(stack: ItemStack, transactionAdder: TransactionConsumer?, peek: Boolean) {
        if (stack.isEmpty || !this.canContain(stack))
            return
        val originalSnapshot = if (transactionAdder == null) null else this.rawItem.createSnapshot()
        val result: ItemStack
        if (this.rawItem.isNotEmpty) {
            val max = this.maxStackQuantityFor(this.rawItem)
            val quantity = this.rawItem.quantity
            val space = max - quantity
            // The slot is full
            if (space == 0)
                return
            // They can't stack
            if (!stack.isSimilarTo(this.rawItem))
                return
            val added = min(stack.quantity, space)
            result = if (peek) this.rawItem.copy() else this.rawItem
            result.quantity += added
            stack.quantity -= added
        } else {
            val max = this.maxStackQuantityFor(stack)
            val added = min(stack.quantity, max)
            result = stack.copy()
            result.quantity = added
            if (!peek)
                this.rawItem = result
            stack.quantity -= added
        }
        if (transactionAdder != null) {
            val resultSnapshot = if (peek) result.asSnapshot() else result.createSnapshot()
            transactionAdder(this, originalSnapshot!!, resultSnapshot)
        }
        if (!peek)
            this.queueUpdate()
    }

    override fun pollFast(predicate: (ItemStackSnapshot) -> Boolean): ItemStack {
        if (!this.testItem(predicate))
            return emptyItemStack()
        val item = this.rawItem
        this.rawItem = emptyItemStack()
        this.queueUpdate()
        return item
    }

    override fun pollFast(limit: Int, predicate: (ItemStackSnapshot) -> Boolean): ItemStack {
        check(limit >= 0) { "Limit may not be negative" }
        if (!this.testItem(predicate))
            return emptyItemStack()
        var item = this.rawItem
        if (item.quantity <= limit) {
            this.rawItem = emptyItemStack()
        } else {
            item.quantity -= limit
            item = item.copy()
            item.quantity = limit
        }
        this.queueUpdate()
        return item
    }

    override fun peek(predicate: (ItemStackSnapshot) -> Boolean): ItemStack {
        if (!this.testItem(predicate))
            return emptyItemStack()
        return this.rawItem.copy()
    }

    override fun peek(limit: Int, predicate: (ItemStackSnapshot) -> Boolean): ItemStack {
        check(limit >= 0) { "Limit may not be negative" }
        if (!this.testItem(predicate))
            return emptyItemStack()
        val copy = this.rawItem.copy()
        copy.quantity = min(limit, copy.quantity)
        return copy
    }

    private fun testItem(predicate: (ItemStackSnapshot) -> Boolean) =
            this.rawItem.isNotEmpty && predicate(this.rawItem.asSnapshot())

    override fun canContain(stack: ItemStack): Boolean =
            this.filter?.test(stack) ?: true

    override fun clear() {
        if (this.rawItem.isEmpty)
            return
        this.rawItem = emptyItemStack()
        this.queueUpdate()
    }

    override fun contains(type: ItemType): Boolean =
            this.rawItem.isNotEmpty && this.rawItem.type == type

    override fun contains(fn: (ItemStackSnapshot) -> Boolean): Boolean =
            fn(this.rawItem.asSnapshot())

    override fun containsAny(stack: ItemStack): Boolean =
            this.rawItem.isNotEmpty && this.rawItem.isSimilarTo(stack)

    override fun contains(stack: ItemStack): Boolean =
            this.rawItem.isNotEmpty && this.rawItem.isEqualTo(stack)

    override fun freeCapacity(): Int = if (this.rawItem.isEmpty) 1 else 0

    override fun totalQuantity(): Int = this.rawItem.quantity

    /**
     * Queues this slot to be updated and trigger the listeners.
     */
    protected fun queueUpdate() {
        for (listener in this.changeListeners)
            listener(this)
        for (tracker in this.trackers)
            tracker.queueSlotChange(this)
    }

    override fun instantiateView(): InventoryView<LanternSlot> = View(this)

    private class View(override val backing: LanternSlot) : AbstractSlotView<LanternSlot>()
}
