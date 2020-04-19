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

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import java.util.List;

public abstract class PeekedTransactionResult {

    private final List<SlotTransaction> transactions;

    /**
     * Constructs a new {@link PeekedTransactionResult}
     * from the given {@link SlotTransaction}s.
     *
     * @param transactions The slot transactions
     */
    protected PeekedTransactionResult(List<SlotTransaction> transactions) {
        this.transactions = ImmutableList.copyOf(transactions);
    }

    /**
     * Gets all the {@link SlotTransaction}s that occur
     * when the transaction result is applied.
     *
     * @return The slot transactions
     */
    public List<SlotTransaction> getTransactions() {
        return this.transactions;
    }

    /**
     * Accepts all the changes that are applied by this result,
     * only {@link SlotTransaction}s that aren't valid will be
     * ignored.
     */
    public void accept() {
        this.transactions.forEach(transaction -> {
            if (transaction.isValid()) {
                transaction.getSlot().set(transaction.getFinal().createStack());
            }
        });
    }

    /**
     * Gets whether this {@link PeekedTransactionResult} is empty.
     *
     * @return Is empty
     */
    public boolean isEmpty() {
        return this.transactions.isEmpty();
    }

    /**
     * Converts this {@link PeekedTransactionResult} into a
     * {@link InventoryTransactionResult}.
     *
     * @return The inventory transaction result
     */
    public InventoryTransactionResult asInventoryTransaction() {
        return asInventoryTransactionBuilder().build();
    }

    protected InventoryTransactionResult.Builder asInventoryTransactionBuilder() {
        return InventoryTransactionResult.builder()
                .type(InventoryTransactionResult.Type.SUCCESS) // Default to success
                .transaction(this.transactions);
    }

    protected MoreObjects.ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .add("transactions", this.transactions);
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }
}
