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
import org.lanternpowered.api.item.inventory.PeekedSetTransactionResult
import org.lanternpowered.api.item.inventory.result.reject
import org.lanternpowered.api.item.inventory.transaction.SlotTransaction

class LanternPeekedSetTransactionResult(
        transactions: List<SlotTransaction>, override val rejectedItem: ItemStackSnapshot
) : LanternPeekedTransactionResult(transactions), PeekedSetTransactionResult {

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
