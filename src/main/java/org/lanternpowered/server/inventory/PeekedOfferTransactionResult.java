/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
