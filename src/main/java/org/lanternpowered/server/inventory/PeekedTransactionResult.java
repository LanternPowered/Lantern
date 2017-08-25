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
import com.google.common.collect.ImmutableList;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import java.util.List;

public abstract class PeekedTransactionResult {

    private final List<SlotTransaction> transactions;

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

    protected MoreObjects.ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(this)
                .add("transactions", this.transactions);
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }
}
