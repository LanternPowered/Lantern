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
import com.google.common.collect.ImmutableList;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import java.util.List;

public final class PeekedPollTransactionResult extends PeekedTransactionResult {

    /**
     * Gets a empty {@link PeekedPollTransactionResult}.
     *
     * @return The empty peeked poll transaction result
     */
    public static PeekedPollTransactionResult empty() {
        return new PeekedPollTransactionResult(ImmutableList.of(), LanternItemStack.empty());
    }

    private final ItemStack polledItem;

    /**
     * Constructs a new {@link PeekedPollTransactionResult}.
     *
     * @param transactions The slot transactions that will occur
     * @param polledItem The polled item stack
     */
    public PeekedPollTransactionResult(List<SlotTransaction> transactions, ItemStack polledItem) {
        super(transactions);
        checkNotNull(polledItem, "polledItem");
        this.polledItem = polledItem;
    }

    /**
     * Gets the {@link ItemStack} that is polled when this
     * result is accepted.
     *
     * @return The polled item stack
     */
    public ItemStack getPolledItem() {
        return this.polledItem;
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("polledItem", this.polledItem);
    }
}
