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

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;

import java.util.Optional;

import javax.annotation.Nullable;

public final class FastOfferResult {

    static final FastOfferResult SUCCESS_NO_REJECTED_ITEM = new FastOfferResult(null, true);

    @Nullable private final ItemStack rejectedItem;
    private final boolean success;

    /**
     * Constructs a {@link FastOfferResult}.
     *
     * @param rejectedItem The rejected item
     * @param success Whether the operation was successful
     */
    public FastOfferResult(@Nullable ItemStack rejectedItem, boolean success) {
        this.rejectedItem = rejectedItem;
        this.success = success;
    }

    /**
     * Gets the rejected {@link ItemStack} of the offer result.
     *
     * @return The rejected item
     */
    public Optional<ItemStack> getRejectedItem() {
        return Optional.ofNullable(this.rejectedItem);
    }

    /**
     * Gets whether the offer operation was a success.
     *
     * @return Is success
     */
    public boolean isSuccess() {
        return this.success;
    }

    /**
     * Converts this {@link FastOfferResult} into a
     * {@link InventoryTransactionResult}.
     *
     * @return The transaction result
     */
    public InventoryTransactionResult asTransactionResult() {
        if (this.rejectedItem == null && this.success) {
            return CachedInventoryTransactionResults.SUCCESS_NO_TRANSACTIONS;
        } else {
            final InventoryTransactionResult.Builder builder = InventoryTransactionResult.builder();
            builder.type(this.success ? InventoryTransactionResult.Type.SUCCESS : InventoryTransactionResult.Type.FAILURE);
            if (this.rejectedItem != null) {
                builder.reject(this.rejectedItem);
            }
            return builder.build();
        }
    }
}
