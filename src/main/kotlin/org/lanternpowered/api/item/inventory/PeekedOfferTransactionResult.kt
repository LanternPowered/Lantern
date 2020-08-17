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

/**
 * The transaction result of a peeked offer operation.
 */
interface PeekedOfferTransactionResult : PeekedTransactionResult {

    /**
     * The items that were rejected. This can occur if the stacks didn't
     * fit all completely in the inventory.
     */
    val rejectedItems: Collection<ItemStackSnapshot>

    /**
     * Represents a [PeekedOfferTransactionResult] when a single stack was offered.
     */
    interface Single : PeekedOfferTransactionResult {

        @Deprecated(message = "Prefer to use rejectedItem instead.", replaceWith = ReplaceWith(""))
        override val rejectedItems: Collection<ItemStackSnapshot>
            get() = listOf(this.rejectedItem)

        /**
         * The item that was rejected.
         */
        val rejectedItem: ItemStackSnapshot
    }
}
