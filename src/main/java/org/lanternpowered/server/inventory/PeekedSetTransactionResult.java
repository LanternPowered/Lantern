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

import com.google.common.base.MoreObjects;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

public class PeekedSetTransactionResult extends PeekedOfferTransactionResult {

    @Nullable private final ItemStack replacedItem;

    /**
     * Constructs a new {@link PeekedSetTransactionResult}.
     *
     * @param type The type of the transaction result
     * @param transactions The slot transactions that will occur
     * @param rejectedItem The rejected item stack, this can occur if the stack doesn't fit the inventory
     * @param replacedItem The replaced item stack
     */
    public PeekedSetTransactionResult(InventoryTransactionResult.Type type, List<SlotTransaction> transactions,
            @Nullable ItemStack rejectedItem, @Nullable ItemStack replacedItem) {
        super(type, transactions, rejectedItem);
        this.replacedItem = replacedItem;
    }

    public Optional<ItemStack> getReplacedItem() {
        return Optional.ofNullable(this.replacedItem);
    }

    @Override
    protected InventoryTransactionResult.Builder asInventoryTransactionBuilder() {
        final InventoryTransactionResult.Builder builder = super.asInventoryTransactionBuilder();
        if (this.replacedItem != null) {
            builder.replace(this.replacedItem);
        }
        return builder;
    }

    @Override
    protected MoreObjects.ToStringHelper toStringHelper() {
        return super.toStringHelper()
                .add("replacedItem", this.replacedItem);
    }
}
