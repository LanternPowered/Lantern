/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) Contributors
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

import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

public interface IInventory extends Inventory {

    @Override
    IInventory parent();

    boolean hasProperty(InventoryProperty<?,?> property);

    boolean hasProperty(Inventory child, InventoryProperty<?,?> property);

    @Override
    <T extends InventoryProperty<?, ?>> Optional<T> getProperty(Class<T> property, @Nullable Object key);

    @Override
    <T extends InventoryProperty<?, ?>> Optional<T> getProperty(Inventory child, Class<T> property, @Nullable Object key);

    /**
     * Offers the {@link ItemStack} fast to this inventory, avoiding
     * the creation of {@link InventoryTransactionResult}s.
     *
     * @param stack The item stack
     * @return The fast offer result
     */
    FastOfferResult offerFast(ItemStack stack);

    PeekOfferTransactionsResult peekOfferFastTransactions(ItemStack stack);

    @Override
    default Optional<ItemStack> poll() {
        return this.poll(stack -> true);
    }

    default Optional<ItemStack> poll(ItemType itemType) {
        checkNotNull(itemType, "itemType");
        return this.poll(stack -> stack.getItem().equals(itemType));
    }

    Optional<ItemStack> poll(Predicate<ItemStack> matcher);

    @Override
    default Optional<ItemStack> poll(int limit) {
        return this.poll(limit, stack -> true);
    }

    default Optional<ItemStack> poll(int limit, ItemType itemType) {
        checkNotNull(itemType, "itemType");
        return this.poll(limit, stack -> stack.getItem().equals(itemType));
    }

    Optional<ItemStack> poll(int limit, Predicate<ItemStack> matcher);

    @Override
    default Optional<ItemStack> peek() {
        return this.peek(stack -> true);
    }

    default Optional<ItemStack> peek(ItemType itemType) {
        checkNotNull(itemType, "itemType");
        return this.peek(stack -> stack.getItem().equals(itemType));
    }

    Optional<ItemStack> peek(Predicate<ItemStack> matcher);

    Optional<PeekPollTransactionsResult> peekPollTransactions(Predicate<ItemStack> matcher);

    @Override
    default Optional<ItemStack> peek(int limit) {
        return this.peek(limit, stack -> true);
    }

    default Optional<ItemStack> peek(int limit, ItemType itemType) {
        checkNotNull(itemType, "itemType");
        return this.peek(limit, stack -> stack.getItem().equals(itemType));
    }

    Optional<ItemStack> peek(int limit, Predicate<ItemStack> matcher);

    Optional<PeekPollTransactionsResult> peekPollTransactions(int limit, Predicate<ItemStack> matcher);

    /**
     * Peeks for the result {@link SlotTransaction}s and {@link InventoryTransactionResult}
     * that would occur if you try to set a item through {@link Inventory#set(ItemStack)}.
     *
     * @param itemStack The item stack to set
     * @return The peeked transaction results
     */
    PeekSetTransactionsResult peekSetTransactions(@Nullable ItemStack itemStack);

    <T extends Inventory> T query(Predicate<Inventory> matcher, boolean nested);

    /**
     * Check whether the supplied item can be inserted into this one of the children of the
     * inventory. Returning false from this method implies that {@link #offer} <b>would
     * always return false</b> for this item.
     *
     * @param stack ItemStack to check
     * @return true if the stack is valid for one of the children of this inventory
     */
    boolean isValidItem(ItemStack stack);

    /**
     * Gets whether the specified {@link Inventory} a child is of this inventory,
     * this includes if it's a child of a child inventory.
     *
     * @param child The child inventory
     * @return Whether the inventory was a child of this inventory
     */
    boolean isChild(Inventory child);

    /**
     * Gets the amount of slots that are present in this inventory.
     *
     * @return The slot count
     */
    int slotCount();
}
