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

import org.lanternpowered.api.item.inventory.InventoryTransactionResultBuilder
import org.lanternpowered.api.item.inventory.InventoryTransactionResultType
import org.lanternpowered.api.item.inventory.ItemStackSnapshot
import org.lanternpowered.api.item.inventory.PeekedOfferTransactionResult
import org.lanternpowered.api.item.inventory.result.reject
import org.lanternpowered.api.item.inventory.transaction.SlotTransaction

class LanternPeekedOfferMultipleItemsTransactionResult(
        transactions: List<SlotTransaction>, override val rejectedItems: Collection<ItemStackSnapshot>
) : LanternPeekedTransactionResult(transactions), PeekedOfferTransactionResult {

    override fun asInventoryTransactionBuilder(): InventoryTransactionResultBuilder {
        val builder = super.asInventoryTransactionBuilder()
        if (this.rejectedItems.isNotEmpty()) {
            builder.type(InventoryTransactionResultType.FAILURE)
            builder.reject(this.rejectedItems)
        } else {
            builder.type(InventoryTransactionResultType.SUCCESS)
        }
        return builder
    }
}

class LanternPeekedOfferSingleItemTransactionResult(
        transactions: List<SlotTransaction>, override val rejectedItem: ItemStackSnapshot
) : LanternPeekedTransactionResult(transactions), PeekedOfferTransactionResult.Single {

    override fun asInventoryTransactionBuilder(): InventoryTransactionResultBuilder {
        val builder = super.asInventoryTransactionBuilder()
        if (!this.rejectedItem.isEmpty) {
            builder.type(InventoryTransactionResultType.FAILURE)
            builder.reject(this.rejectedItem)
        } else {
            builder.type(InventoryTransactionResultType.SUCCESS)
        }
        return builder
    }
}
