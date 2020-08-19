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
package org.lanternpowered.server.inventory

import it.unimi.dsi.fastutil.objects.Object2IntMap
import it.unimi.dsi.fastutil.objects.Object2IntMaps
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import org.lanternpowered.api.entity.player.Player
import org.lanternpowered.api.item.ItemType
import org.lanternpowered.api.item.inventory.Inventory
import org.lanternpowered.api.item.inventory.InventoryTransactionResult
import org.lanternpowered.api.item.inventory.InventoryTransactionResultType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.PollInventoryTransactionResult
import org.lanternpowered.api.item.inventory.slot.Slot
import org.lanternpowered.api.item.inventory.emptyItemStack
import org.lanternpowered.api.item.inventory.emptyItemStackSnapshot
import org.lanternpowered.api.item.inventory.query.QueryTypes
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import org.lanternpowered.api.item.inventory.stack.asSnapshot
import org.lanternpowered.api.item.inventory.stack.asStack
import org.lanternpowered.api.item.inventory.stack.isNotEmpty
import org.lanternpowered.api.item.inventory.stack.isSimilarTo
import org.lanternpowered.api.item.inventory.transaction.SlotTransaction
import org.lanternpowered.api.util.collections.toImmutableList
import org.spongepowered.api.item.inventory.Carrier
import org.spongepowered.api.item.inventory.type.ViewableInventory

internal fun Sequence<AbstractInventory>.slots(): Sequence<AbstractSlot> =
        this.flatMap { inventory ->
            if (inventory is AbstractSlot)
                return@flatMap sequenceOf(inventory)
            inventory.children().asSequence().slots()
        }

abstract class AbstractChildrenInventory : AbstractMutableInventory() {

    companion object {

        private const val INVALID_SLOT_INDEX = -1
    }

    private lateinit var children: List<AbstractMutableInventory>

    private lateinit var slotsByIndex: List<AbstractSlot>
    private lateinit var slotsToIndex: Object2IntMap<AbstractSlot>

    private fun init(children: List<AbstractMutableInventory>, slots: Iterable<AbstractSlot>) {
        val slotsToIndex = Object2IntOpenHashMap<AbstractSlot>()
        slotsToIndex.defaultReturnValue(INVALID_SLOT_INDEX)
        for ((index, slot) in slots.withIndex())
            slotsToIndex[slot] = index
        this.slotsToIndex = Object2IntMaps.unmodifiable(slotsToIndex)
        this.slotsByIndex = slots.toImmutableList()
        this.children = children.toImmutableList()
    }

    protected open fun init(children: List<AbstractMutableInventory>, slots: List<AbstractSlot>) {
        this.init(children, slots as Iterable<AbstractSlot>)
    }

    protected open fun init(children: List<AbstractMutableInventory>) {
        this.init(children, children.asSequence().slots()
                .distinctBy { slot -> slot.original() }
                .asIterable())
    }

    override fun addSlotChangeListener(listener: (ExtendedSlot) -> Unit) {
        for (child in this.children)
            child.addSlotChangeListener(listener)
    }

    override fun children(): List<AbstractMutableInventory> = this.children
    override fun slots(): List<ExtendedSlot> = this.slotsByIndex

    override fun slotOrNull(index: Int): ExtendedSlot? =
            if (index in this.slotsByIndex.indices) this.slotsByIndex[index] else null

    override fun slotIndexOrNull(slot: Slot): Int? {
        slot as AbstractSlot
        val index = this.slotsToIndex.getInt(slot)
        return if (index == INVALID_SLOT_INDEX) null else index
    }

    override fun setCarrier(carrier: Carrier?, override: Boolean) {
        super.setCarrier(carrier, override)

        for (child in this.children())
            child.setCarrier(carrier, override)
    }

    override fun addViewer(player: Player) {
        super.addViewer(player)

        for (child in this.children())
            child.addViewer(player)
    }

    override fun removeViewer(player: Player) {
        super.removeViewer(player)

        for (child in this.children())
            child.removeViewer(player)
    }

    override fun clear() {
        for (child in this.children())
            child.clear()
    }

    final override fun capacity(): Int =
            this.children().sumBy { child -> child.capacity() }

    final override fun totalQuantity(): Int =
            this.children().sumBy { child -> child.totalQuantity() }

    final override fun freeCapacity(): Int =
            this.children().sumBy { child -> child.freeCapacity() }

    final override fun contains(stack: ItemStack): Boolean =
            this.children().any { child -> child.contains(stack) }

    final override fun contains(type: ItemType): Boolean =
            this.children().any { child -> child.contains(type) }

    final override fun containsAny(stack: ItemStack): Boolean =
            this.children().any { child -> child.containsAny(stack) }

    final override fun canContain(stack: ItemStack): Boolean =
            this.children().any { child -> child.canContain(stack) }

    final override fun poll(predicate: (ItemStackSnapshot) -> Boolean): PollInventoryTransactionResult {
        for (child in this.children()) {
            val result = child.poll(predicate)
            if (result.polledItem.isNotEmpty)
                return result
        }
        return InventoryTransactionResults.rejectPoll()
    }

    final override fun pollFast(predicate: (ItemStackSnapshot) -> Boolean): ItemStack {
        for (child in this.children()) {
            val result = child.pollFast(predicate)
            if (result.isNotEmpty)
                return result
        }
        return emptyItemStack()
    }

    final override fun poll(limit: Int, predicate: (ItemStackSnapshot) -> Boolean): PollInventoryTransactionResult {
        check(limit >= 0) { "Limit may not be negative" }
        if (limit == 0)
            return InventoryTransactionResults.rejectPoll()
        var remaining = limit
        var matcher = predicate
        var combined: ItemStack? = null
        val transactions: MutableList<SlotTransaction> = mutableListOf()
        for (child in this.children()) {
            val result = child.poll(remaining, matcher)
            val polled = result.polledItem.asStack()
            if (polled.isNotEmpty) {
                transactions += result.slotTransactions
                remaining -= polled.quantity
                if (combined == null) {
                    combined = polled
                    matcher = SimilarItemMatcher(polled)
                } else {
                    combined.quantity += polled.quantity
                }
                if (remaining == 0)
                    break
            }
        }
        val snapshot = combined?.asSnapshot() ?: emptyItemStackSnapshot()
        return InventoryTransactionResult.builder()
                .type(InventoryTransactionResultType.SUCCESS)
                .transaction(transactions)
                .poll(snapshot)
                .build()
    }

    final override fun pollFast(limit: Int, predicate: (ItemStackSnapshot) -> Boolean): ItemStack =
            this.peekOrPollFast(limit, predicate) { child, remaining, matcher -> child.pollFast(remaining, matcher) }

    final override fun peek(predicate: (ItemStackSnapshot) -> Boolean): ItemStack {
        for (child in this.children()) {
            val result = child.peek(predicate)
            if (result.isNotEmpty)
                return result
        }
        return emptyItemStack()
    }

    final override fun peek(limit: Int, predicate: (ItemStackSnapshot) -> Boolean): ItemStack =
            this.peekOrPollFast(limit, predicate) { child, remaining, matcher -> child.peek(remaining, matcher) }

    private fun peekOrPollFast(
            limit: Int,
            predicate: (ItemStackSnapshot) -> Boolean,
            fn: (AbstractInventory, Int, (ItemStackSnapshot) -> Boolean) -> ItemStack
    ): ItemStack {
        check(limit >= 0) { "Limit may not be negative" }
        if (limit == 0)
            return emptyItemStack()
        var remaining = limit
        var matcher = predicate
        var combined: ItemStack? = null
        for (child in this.children()) {
            val polled = fn(child, remaining, matcher)
            if (polled.isNotEmpty) {
                remaining -= polled.quantity
                if (combined == null) {
                    combined = polled
                    matcher = SimilarItemMatcher(polled)
                } else {
                    combined.quantity += polled.quantity
                }
                if (remaining == 0)
                    break
            }
        }
        return combined ?: emptyItemStack()
    }

    private class SimilarItemMatcher(val stack: ItemStack) : (ItemStackSnapshot) -> Boolean {
        override fun invoke(other: ItemStackSnapshot): Boolean = this.stack.isSimilarTo(other)
    }

    final override fun peekOfferAndConsume(stack: ItemStack, transactionAdder: TransactionConsumer?) =
            this.offerAndConsume0(stack) { child -> child.peekOfferAndConsume(stack, transactionAdder) }

    final override fun offerAndConsume(stack: ItemStack, transactionAdder: TransactionConsumer?) =
            this.offerAndConsume0(stack) { child -> child.offerAndConsume(stack, transactionAdder) }

    private fun offerAndConsume0(stack: ItemStack, fn: (child: AbstractInventory) -> Unit) {
        if (stack.isEmpty)
            return
        val processed = mutableSetOf<Inventory>()
        // First try to offer to slots which contain a similar item
        val inventory = this.query(QueryTypes.ITEM_STACK_IGNORE_QUANTITY, stack)
        if (inventory is AbstractChildrenInventory) {
            inventory.offerAndConsume0(stack, processed, fn)
            if (stack.isEmpty)
                return
        }
        this.offerAndConsume0(stack, processed, fn)
    }

    private fun offerAndConsume0(stack: ItemStack, processed: MutableSet<Inventory>, fn: (child: AbstractInventory) -> Unit) {
        for (inventory in this.children()) {
            var delegate = inventory
            // Check for the delegate slot if present
            while (delegate is AbstractForwardingSlot)
                delegate = delegate.delegateSlot
            if (!processed.add(delegate))
                continue
            fn(inventory)
            // Stack got consumed, stop fast
            if (stack.isEmpty)
                return
        }
    }

    override fun toViewable(): ViewableInventory? {
        TODO("Not yet implemented")
    }
}
