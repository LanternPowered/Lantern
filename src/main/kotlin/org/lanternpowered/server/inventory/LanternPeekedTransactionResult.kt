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
import org.lanternpowered.api.item.inventory.InventoryTransactionResultBuilder
import org.lanternpowered.api.item.inventory.InventoryTransactionResultType
import org.lanternpowered.api.item.inventory.PeekedTransactionResult
import org.lanternpowered.api.item.inventory.transaction.SlotTransaction

open class LanternPeekedTransactionResult(override val transactions: List<SlotTransaction>) : PeekedTransactionResult {

    override val isEmpty: Boolean
        get() = this.transactions.isEmpty()

    override fun accept(): InventoryTransactionResult {
        this.acceptFast()
        return this.asInventoryTransactionBuilder().build()
    }

    override fun acceptFast() {
        for (transaction in this.transactions) {
            if (!transaction.isValid)
                continue
            transaction.slot.set(transaction.final.createStack())
        }
    }

    protected open fun asInventoryTransactionBuilder(): InventoryTransactionResultBuilder =
            InventoryTransactionResult.builder()
                    .type(InventoryTransactionResultType.SUCCESS) // Default to success
                    .transaction(this.transactions)
}
