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

import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;

import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

public interface IInventory extends Inventory {

    @Override
    IInventory parent();

    /**
     * Gets the root {@link IInventory}.
     *
     * @return The root inventory
     */
    IInventory root();

    <T extends Inventory> Iterable<T> orderedSlots();

    /**
     * Adds a {@link SlotChangeListener} to
     * this {@link Inventory}.
     *
     * @param listener The listener
     */
    void addChangeListener(SlotChangeListener listener);

    /**
     * Adds a {@link InventoryViewerListener} to this {@link Inventory}.
     *
     * @param listener The listener
     */
    void addViewListener(InventoryViewerListener listener);

    /**
     * Adds a {@link InventoryCloseListener} to this {@link Inventory}.
     *
     * @param listener The listener
     */
    void addCloseListener(InventoryCloseListener listener);

    /**
     * Polls the first available stack with the specific {@link ItemType}.
     *
     * @param itemType The item type
     * @return The polled item stack, if found
     * @see #poll()
     */
    Optional<ItemStack> poll(ItemType itemType);

    /**
     * Polls the first available stack that is matched by the {@link Predicate}.
     *
     * @param matcher The matcher
     * @return The polled item stack, if found
     * @see #poll()
     */
    Optional<ItemStack> poll(Predicate<ItemStack> matcher);

    Optional<ItemStack> poll(int limit, ItemType itemType);

    Optional<ItemStack> poll(int limit, Predicate<ItemStack> matcher);

    Optional<ItemStack> peek(ItemType itemType);

    Optional<ItemStack> peek(Predicate<ItemStack> matcher);

    Optional<ItemStack> peek(int limit, ItemType itemType);

    Optional<ItemStack> peek(int limit, Predicate<ItemStack> matcher);

    Optional<PeekedPollTransactionResult> peekPoll(Predicate<ItemStack> matcher);

    Optional<PeekedPollTransactionResult> peekPoll(int limit, Predicate<ItemStack> matcher);

    PeekedOfferTransactionResult peekOffer(ItemStack itemStack);

    /**
     * Peeks for the result {@link SlotTransaction}s and {@link InventoryTransactionResult}
     * that would occur if you try to set a item through {@link Inventory#set(ItemStack)}.
     *
     * @param itemStack The item stack to set
     * @return The peeked transaction result
     */
    PeekedSetTransactionResult peekSet(@Nullable ItemStack itemStack);

    /**
     * Check whether the supplied item can be inserted into this one of the children of the
     * inventory. Returning false from this method implies that {@link #offer} <b>would
     * always return false</b> for this item.
     *
     * @param stack ItemStack to check
     * @return True if the stack is valid for at least one of the children of this inventory
     */
    boolean isValidItem(ItemStack stack);

    <T extends Inventory> T queryNot(Class<?>... types);

    @Override
    IInventory intersect(Inventory inventory);

    @Override
    IInventory union(Inventory inventory);

    /**
     * {@inheritDoc}
     */
    @Override
    InventoryTransactionResult set(@Nullable ItemStack stack);

    /**
     * Similar to the {@link #set(ItemStack)} method but ignores
     * the checking whether a specific item can be put into a
     * {@link Slot}.
     *
     * @param stack The stack to insert
     * @return The transaction result
     */
    InventoryTransactionResult setForced(@Nullable ItemStack stack);
}
