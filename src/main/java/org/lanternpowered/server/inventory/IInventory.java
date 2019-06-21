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

import org.lanternpowered.server.item.predicate.ItemPredicate;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryTransformation;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.query.QueryOperation;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.item.inventory.type.ViewableInventory;

import java.util.Optional;
import java.util.function.Predicate;

public interface IInventory extends Inventory {

    @Override
    IInventory parent();

    @Override
    IInventory root();

    @Override
    IQueryInventory query(QueryOperation<?>... operations);

    /**
     * Gets the {@link EmptyInventory} that should be used by this
     * inventory when queries fail.
     *
     * @return The empty inventory
     */
    EmptyInventory empty();

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
     * Try to put an {@link ItemStack} into this inventory. The return value
     * will be {@code true} only when the entire item stack fits the inventory.
     *
     * <p>The size of the supplied stack is reduced by the number of items
     * successfully consumed by the inventory.</p>
     *
     * <p>Any rejected items are also available in the transaction result.</p>
     *
     * <p>Unlike {@link #set}, this method's general contract does not permit
     * items in the Inventory to be replaced.</p>
     *
     * @param stack A stack of items to insert into this inventory
     * @return Whether the complete stack was successfully inserted into this inventory
     */
    boolean offerFast(ItemStack stack);

    /**
     * Polls the first available stack with the specific {@link ItemType}.
     *
     * @param itemType The item type
     * @return The polled item stack, if found
     * @see #poll()
     */
    LanternItemStack poll(ItemType itemType);

    LanternItemStack poll(ItemPredicate matcher);

    /**
     * Polls the first available stack that is matched by the {@link Predicate}.
     *
     * @param matcher The matcher
     * @return The polled item stack, if found
     * @see #poll()
     */
    LanternItemStack poll(Predicate<ItemStack> matcher);

    @Override
    LanternItemStack poll();

    @Override
    LanternItemStack poll(int limit);

    LanternItemStack poll(int limit, ItemType itemType);

    LanternItemStack poll(int limit, ItemPredicate matcher);

    LanternItemStack poll(int limit, Predicate<ItemStack> matcher);

    @Override
    LanternItemStack peek();

    @Override
    LanternItemStack peek(int limit);

    LanternItemStack peek(ItemType itemType);

    LanternItemStack peek(ItemPredicate matcher);

    LanternItemStack peek(Predicate<ItemStack> matcher);

    LanternItemStack peek(int limit, ItemType itemType);

    LanternItemStack peek(int limit, ItemPredicate matcher);

    LanternItemStack peek(int limit, Predicate<ItemStack> matcher);

    PeekedPollTransactionResult peekPoll(ItemPredicate matcher);

    PeekedPollTransactionResult peekPoll(Predicate<ItemStack> matcher);

    PeekedPollTransactionResult peekPoll(int limit, ItemPredicate matcher);

    PeekedPollTransactionResult peekPoll(int limit, Predicate<ItemStack> matcher);

    /**
     * Peeks a result to put an {@link ItemStack} into this Inventory. The
     * {@link PeekedOfferTransactionResult} will be a success only when the
     * entire item stack fits the inventory.
     *
     * <p>The size of the supplied stack is reduced by the number of items
     * successfully consumed by the inventory.</p>
     *
     * <p>Any rejected items are also available in the transaction result.</p>
     *
     * <p>Unlike {@link #set}, this method's general contract does not permit
     * items in the Inventory to be replaced.</p>
     *
     * @param stack The stack of items to offer
     * @return The peeked offer transaction result
     */
    PeekedOfferTransactionResult peekOffer(ItemStack stack);

    /**
     * Peeks for the result {@link SlotTransaction}s and {@link InventoryTransactionResult}
     * that would occur if you try to set a item through {@link Inventory#set(ItemStack)}.
     *
     * @param itemStack The item stack to set
     * @return The peeked transaction result
     */
    PeekedSetTransactionResult peekSet(ItemStack itemStack);

    /**
     * Check whether the supplied item can be inserted into this one of the children of the
     * inventory. Returning false from this method implies that {@link #offer} <b>would
     * always return false</b> for this item.
     *
     * @param stack ItemStack to check
     * @return True if the stack is valid for at least one of the children of this inventory
     */
    boolean isValidItem(ItemStack stack);

    @Override
    IInventory intersect(Inventory inventory);

    @Override
    IInventory union(Inventory inventory);

    /**
     * Similar to the {@link #set(ItemStack)} method but ignores
     * the checking whether a specific item can be put into a
     * {@link Slot}.
     *
     * @param stack The stack to insert
     * @return The transaction result
     */
    default InventoryTransactionResult setForced(ItemStack stack) {
        return set(stack, true);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    default InventoryTransactionResult set(ItemStack stack) {
        return set(stack, false);
    }

    InventoryTransactionResult set(ItemStack stack, boolean force);

    @Override
    default IInventory transform(InventoryTransformation transformation) {
        return (IInventory) Inventory.super.transform(transformation);
    }

    /**
     * Gets the index of the {@link Slot} in this ordered inventory,
     * may return {@code -1} if the slot was not found.
     *
     * @param slot The slot
     * @return The slot index
     */
    int getSlotIndex(Slot slot);

    @Override
    default boolean hasChildren() {
        return !children().isEmpty();
    }

    @Override
    default Optional<ViewableInventory> asViewable() {
        return this instanceof ViewableInventory ? Optional.of((ViewableInventory) this) : Optional.empty();
    }
}
