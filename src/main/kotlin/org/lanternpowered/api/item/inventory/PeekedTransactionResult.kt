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
package org.lanternpowered.api.item.inventory

import org.lanternpowered.api.item.inventory.transaction.SlotTransaction

/**
 * Represents a result of a peek operation.
 */
interface PeekedTransactionResult {

    /**
     * Gets all the [SlotTransaction]s that occur when the
     * transaction result is applied.
     *
     * @return The slot transactions
     */
    val transactions: List<SlotTransaction>

    /**
     * Gets whether this [PeekedTransactionResult] is empty.
     *
     * @return Is empty
     */
    val isEmpty: Boolean

    /**
     * Accepts all the changes that are applied by this result,
     * only [SlotTransaction]s that aren't valid will be ignored.
     *
     * Returns the [InventoryTransactionResult] that happened by
     * accepting this.
     */
    fun accept(): InventoryTransactionResult

    /**
     * Accepts all the changes that are applied by this result,
     * only [SlotTransaction]s that aren't valid will be ignored.
     *
     * This is the fast variant of [accept], use this if you don't care
     * about the transaction result.
     */
    fun acceptFast()
}
