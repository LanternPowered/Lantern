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

import org.lanternpowered.api.item.inventory.InventoryTransactionResult
import org.lanternpowered.api.item.inventory.InventoryTransactionResultType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.PollInventoryTransactionResult
import org.lanternpowered.api.item.inventory.slot.Slot
import org.lanternpowered.api.item.inventory.emptyItemStackSnapshot
import org.lanternpowered.api.item.inventory.result.reject
import org.lanternpowered.api.item.inventory.slot.ExtendedSlot
import org.lanternpowered.api.item.inventory.stack.asSnapshot
import org.lanternpowered.api.util.optional.asOptional
import org.lanternpowered.api.util.optional.emptyOptional
import org.spongepowered.api.item.inventory.transaction.SlotTransaction
import java.util.Optional

@Suppress("DEPRECATION")
abstract class AbstractSlot : AbstractMutableInventory(), ExtendedSlot {

    @Suppress("LeakingThis")
    private val slots = listOf(this)

    /**
     * Direct access to the [ItemStack] that's stored in this slot.
     */
    abstract var rawItem: ItemStack

    /**
     * Adds the [SlotChangeTracker].
     *
     * @param tracker The slot change tracker
     */
    abstract fun addTracker(tracker: SlotChangeTracker)

    /**
     * Removes the [SlotChangeTracker].
     *
     * @param tracker The slot change tracker
     */
    abstract fun removeTracker(tracker: SlotChangeTracker)

    override fun viewedSlot(): ExtendedSlot = this

    abstract fun setAndConsume(stack: ItemStack, force: Boolean, transactionAdder: TransactionConsumer?)

    final override fun safeSet(stack: ItemStack): InventoryTransactionResult = this.set(stack, false)

    final override fun forceSet(stack: ItemStack): InventoryTransactionResult = this.set(stack, true)

    private fun set(stack: ItemStack, force: Boolean): InventoryTransactionResult {
        val builder = InventoryTransactionResult.builder()
        val quantity = stack.quantity
        this.setAndConsume(stack, force) { slot, original, replacement ->
            builder.transaction(SlotTransaction(slot, original, replacement))
        }
        if (stack.isEmpty) {
            builder.type(InventoryTransactionResultType.SUCCESS)
        } else {
            builder.type(InventoryTransactionResultType.FAILURE)
            builder.reject(stack.createSnapshot())
        }
        stack.quantity = quantity
        return builder.reject().build()
    }

    final override fun safeSetFast(stack: ItemStack): Boolean = this.setFast(stack, false)

    final override fun forceSetFast(stack: ItemStack): Boolean = this.setFast(stack, true)

    private fun setFast(stack: ItemStack, force: Boolean): Boolean {
        val quantity = stack.quantity
        this.setAndConsume(stack, force, null)
        val success = stack.isEmpty
        stack.quantity = quantity
        return success
    }

    final override fun poll(predicate: (ItemStackSnapshot) -> Boolean): PollInventoryTransactionResult =
            this.buildPollResult(this.pollFast(predicate))

    final override fun poll(limit: Int, predicate: (ItemStackSnapshot) -> Boolean): PollInventoryTransactionResult =
            this.buildPollResult(this.pollFast(limit, predicate))

    private fun buildPollResult(stack: ItemStack): PollInventoryTransactionResult {
        if (stack.isEmpty)
            return InventoryTransactionResults.rejectPoll()
        val snapshot = stack.asSnapshot()
        val transaction = SlotTransaction(this, snapshot, emptyItemStackSnapshot())
        return InventoryTransactionResult.builder()
                .type(InventoryTransactionResultType.SUCCESS)
                .transaction(transaction)
                .poll(snapshot)
                .build()
    }

    final override fun safeSet(index: Int, stack: ItemStack): InventoryTransactionResult =
            if (index == 0) this.safeSet(stack) else InventoryTransactionResults.rejectNoSlot(stack)

    final override fun forceSet(index: Int, stack: ItemStack): InventoryTransactionResult =
            if (index == 0) this.forceSet(stack) else InventoryTransactionResults.rejectNoSlot(stack)

    final override fun slotOrNull(index: Int): ExtendedSlot? =
            if (index == 0) this else null

    override fun slotIndexOrNull(slot: Slot): Int? =
            if (slot == this) 0 else null

    override fun getSlot(index: Int): Optional<Slot> =
            if (index == 0) this.asOptional() else emptyOptional()

    override fun peekAt(index: Int): Optional<ItemStack> =
            if (index == 0) this.peek().asOptional() else emptyOptional()

    override fun offer(index: Int, stack: ItemStack): InventoryTransactionResult =
            if (index == 0) this.offer(stack) else InventoryTransactionResults.rejectNoSlot(stack)

    override fun pollFrom(index: Int): PollInventoryTransactionResult =
            if (index == 0) this.poll() else InventoryTransactionResults.rejectPollNoSlot()

    override fun pollFrom(index: Int, limit: Int): PollInventoryTransactionResult =
            if (index == 0) this.poll(limit) else InventoryTransactionResults.rejectPollNoSlot()

    override fun slots(): List<ExtendedSlot> = listOf(this)
    override fun children(): List<AbstractMutableInventory> = emptyList()
    override fun capacity(): Int = 1
}
