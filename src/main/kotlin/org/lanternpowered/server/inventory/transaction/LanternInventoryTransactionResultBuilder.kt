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
import org.lanternpowered.api.item.inventory.InventoryTransactionResultBuilder
import org.lanternpowered.api.item.inventory.InventoryTransactionResultType
import org.lanternpowered.api.item.inventory.ItemStack
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.PollInventoryTransactionResult
import org.lanternpowered.api.item.inventory.PollInventoryTransactionResultBuilder
import org.lanternpowered.api.item.inventory.transaction.SlotTransaction
import org.lanternpowered.api.util.collections.toImmutableList

class LanternInventoryTransactionResultBuilder : PollInventoryTransactionResultBuilder {

    private var type: InventoryTransactionResultType? = null
    private var rejected: MutableList<ItemStackSnapshot>? = null
    private var polled: MutableList<ItemStackSnapshot>? = null
    private var transactions: MutableList<SlotTransaction>? = null

    override fun from(value: InventoryTransactionResult): InventoryTransactionResultBuilder = this.apply {
        value as LanternInventoryTransactionResult
        this.type = value.type
        val rejected = this.rejected
        if (rejected == null) {
            this.rejected = value.rejectedItems.toMutableList()
        } else {
            rejected.clear()
            rejected.addAll(value.rejectedItems)
        }
        val polled = this.polled
        if (polled == null) {
            this.polled = value.polledItems.toMutableList()
        } else {
            polled.clear()
            polled.addAll(value.polledItems)
        }
        val transactions = this.transactions
        if (transactions == null) {
            this.transactions = value.slotTransactions.toMutableList()
        } else {
            transactions.clear()
            transactions.addAll(value.slotTransactions)
        }
    }

    override fun reject(vararg itemStacks: ItemStack): InventoryTransactionResultBuilder =
            this.reject(itemStacks.asList().map(ItemStack::createSnapshot))

    override fun reject(itemStacks: Iterable<ItemStackSnapshot>): InventoryTransactionResultBuilder = this.apply {
        val rejected = this.rejected ?: mutableListOf<ItemStackSnapshot>().also { this.rejected = it }
        rejected += itemStacks
    }

    override fun transaction(vararg slotTransactions: SlotTransaction): InventoryTransactionResultBuilder =
            this.transaction(slotTransactions.asList())

    override fun transaction(slotTransactions: Iterable<SlotTransaction>): InventoryTransactionResultBuilder = this.apply {
        val transactions = this.transactions ?: mutableListOf<SlotTransaction>().also { this.transactions = it }
        transactions += slotTransactions
    }

    override fun reset(): InventoryTransactionResultBuilder = this.apply {
        this.transactions?.clear()
        this.rejected?.clear()
        this.polled?.clear()
        this.type = null
    }

    override fun type(type: InventoryTransactionResultType): InventoryTransactionResultBuilder = this.apply {
        this.type = type
    }

    override fun poll(itemStack: ItemStackSnapshot): PollInventoryTransactionResultBuilder = this.apply {
        val polled = this.polled ?: mutableListOf<ItemStackSnapshot>().also { this.polled = it }
        polled += itemStack
    }

    override fun build(): PollInventoryTransactionResult {
        val type = checkNotNull(this.type) { "The type must be set" }
        val rejected = this.rejected?.toImmutableList() ?: emptyList<ItemStackSnapshot>()
        val polled = this.polled?.toImmutableList() ?: emptyList<ItemStackSnapshot>()
        val transactions = this.transactions?.toImmutableList() ?: emptyList<SlotTransaction>()
        return LanternInventoryTransactionResult(type, transactions, rejected, polled)
    }
}
