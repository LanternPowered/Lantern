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
package org.lanternpowered.server.inventory;

import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import java.util.List;

public final class PeekedSetTransactionResult extends PeekedOfferTransactionResult {

    /**
     * Constructs a new {@link PeekedSetTransactionResult}.
     *
     * @param transactions The slot transactions that will occur
     * @param rejectedItem The rejected item stack, this can occur if the stack doesn't fit the inventory
     */
    public PeekedSetTransactionResult(List<SlotTransaction> transactions, ItemStackSnapshot rejectedItem) {
        super(transactions, rejectedItem);
    }
}
