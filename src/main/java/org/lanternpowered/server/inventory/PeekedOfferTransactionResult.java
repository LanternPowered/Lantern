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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.MoreObjects;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import java.util.Collections;
import java.util.List;

public class PeekedOfferTransactionResult extends PeekedTransactionResult {

    private final ItemStackSnapshot rejectedItem;

    /**
     * Constructs a new {@link PeekedOfferTransactionResult}.
     *
     * @param transactions The slot transactions that will occur
     * @param rejectedItem The rejected item stack, this can occur if the stack doesn't fit the inventory
     */
    public PeekedOfferTransactionResult(List<SlotTransaction> transactions, ItemStackSnapshot rejectedItem) {
        super(transactions);
        checkNotNull(rejectedItem, "rejectedItem");
        this.rejectedItem = rejectedItem;
    }

    /**
     * Gets the {@link ItemStack} that wouldn't be added to the inventory
     * if this result was accepted. This can occur if the set stack doesn't
     * fit the inventory.
     *
     * @return The rejected item stack
     */
    public ItemStackSnapshot getRejectedItem() {
        return this.rejectedItem;
    }

    @Override
    protected InventoryTransactionResult.Builder asInventoryTransactionBuilder() {
        final InventoryTransactionResult.Builder builder = super.asInventoryTransactionBuilder();
        if (this.rejectedItem.isEmpty()) {
            // The complete stack is consumed, so success
            builder.type(InventoryTransactionResult.Type.SUCCESS);
        } else {
            builder.type(InventoryTransactionResult.Type.FAILURE);
            builder.reject(Collections.singleton(this.rejectedItem));
        }
        return builder;
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("rejectedItem", this.rejectedItem);
    }
}
