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
package org.lanternpowered.server.inventory.transaction

import org.lanternpowered.api.item.inventory.InventoryTransactionResult
import org.lanternpowered.api.item.inventory.InventoryTransactionResultType
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.PollInventoryTransactionResult
import org.lanternpowered.api.item.inventory.emptyItemStackSnapshot
import org.lanternpowered.api.item.inventory.slot.Slot
import org.lanternpowered.api.item.inventory.transaction.SlotTransaction
import org.lanternpowered.api.util.ToStringHelper
import org.lanternpowered.api.util.collections.contentToString
import org.lanternpowered.api.util.collections.immutableListBuilderOf
import org.lanternpowered.api.util.collections.toImmutableList
import org.lanternpowered.server.inventory.AbstractSlot
import org.lanternpowered.server.inventory.original

class LanternInventoryTransactionResult(
        private val type: InventoryTransactionResultType,
        private val transactions: List<SlotTransaction>,
        private val rejected: List<ItemStackSnapshot>,
        private val polled: List<ItemStackSnapshot>
) : PollInventoryTransactionResult {

    override fun getType(): InventoryTransactionResultType = this.type
    override fun getPolledItems(): List<ItemStackSnapshot> = this.polled
    override fun getSlotTransactions(): List<SlotTransaction> = this.transactions
    override fun getPolledItem(): ItemStackSnapshot = this.polled.firstOrNull() ?: emptyItemStackSnapshot()
    override fun getRejectedItems(): List<ItemStackSnapshot> = this.rejected

    override fun and(other: InventoryTransactionResult): InventoryTransactionResult {
        val resultType = if (this.type == InventoryTransactionResultType.ERROR ||
                other.type == InventoryTransactionResultType.ERROR) {
            InventoryTransactionResultType.ERROR
        } else if (this.type == InventoryTransactionResultType.FAILURE ||
                other.type == InventoryTransactionResultType.FAILURE) {
            InventoryTransactionResultType.FAILURE
        } else if (this.type == InventoryTransactionResultType.NO_SLOT &&
                other.type == InventoryTransactionResultType.NO_SLOT) {
            InventoryTransactionResultType.NO_SLOT
        } else {
            InventoryTransactionResultType.SUCCESS
        }
        val transactions = mutableMapOf<Slot, SlotTransaction>()
        for (thisTransaction in this.transactions) {
            if (!thisTransaction.isValid)
                continue
            transactions[(thisTransaction.slot as AbstractSlot).original()] = thisTransaction
        }
        for (thatTransaction in other.slotTransactions) {
            if (!thatTransaction.isValid)
                continue
            transactions.compute((thatTransaction.slot as AbstractSlot).original()) { _, thisTransaction ->
                if (thisTransaction == null || !thisTransaction.isValid)
                    return@compute thatTransaction
                SlotTransaction(thisTransaction.slot, thisTransaction.original, thatTransaction.final)
            }
        }
        other as LanternInventoryTransactionResult
        val rejected = immutableListBuilderOf<ItemStackSnapshot>()
                .addAll(this.rejected).addAll(other.rejected).build()
        val polled = immutableListBuilderOf<ItemStackSnapshot>()
                .addAll(this.polled).addAll(other.polled).build()
        return LanternInventoryTransactionResult(resultType,
                transactions.values.toImmutableList(), rejected, polled)
    }

    override fun revert() {
        for (transaction in this.transactions.reversed()) {
            if (transaction.isValid)
                transaction.slot.set(transaction.original.createStack())
        }
    }

    override fun revertOnFailure(): Boolean {
        if (this.type != InventoryTransactionResultType.FAILURE)
            return false
        this.revert()
        return true
    }

    override fun toString(): String = ToStringHelper(this)
            .add("type", this.type)
            .add("transactions", this.transactions.contentToString())
            .add("polled", if (this.polled.isEmpty()) null else this.polled.contentToString())
            .add("rejected", if (this.rejected.isEmpty()) null else this.rejected.contentToString())
            .omitNullValues()
            .toString()
}
