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

import static org.lanternpowered.server.text.translation.TranslationHelper.tr;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.translation.Translation;

import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

class LanternEmptyInventory extends AbstractInventory implements EmptyInventory {

    static class Name {
        static final Translation INSTANCE = tr("inventory.empty.name");
    }

    @Override
    public EmptyInventory empty() {
        return this;
    }

    @Override
    public void addChangeListener(SlotChangeListener listener) {
    }

    @Override
    public void addViewListener(InventoryViewerListener listener) {
    }

    @Override
    public void addCloseListener(InventoryCloseListener listener) {
    }

    @Override
    public Optional<ItemStack> poll(ItemType itemType) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> poll(Predicate<ItemStack> matcher) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> poll(int limit, ItemType itemType) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> poll(int limit, Predicate<ItemStack> matcher) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> peek(ItemType itemType) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> peek(Predicate<ItemStack> matcher) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> peek(int limit, ItemType itemType) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> peek(int limit, Predicate<ItemStack> matcher) {
        return Optional.empty();
    }

    @Override
    public PeekedOfferTransactionResult peekOffer(ItemStack itemStack) {
        return new PeekedOfferTransactionResult(InventoryTransactionResult.Type.FAILURE, ImmutableList.of(), itemStack);
    }

    @Override
    public Optional<PeekedPollTransactionResult> peekPoll(Predicate<ItemStack> matcher) {
        return Optional.empty();
    }

    @Override
    public Optional<PeekedPollTransactionResult> peekPoll(int limit, Predicate<ItemStack> matcher) {
        return Optional.empty();
    }

    @Override
    public PeekedSetTransactionResult peekSet(@Nullable ItemStack itemStack) {
        return new PeekedSetTransactionResult(InventoryTransactionResult.Type.FAILURE, ImmutableList.of(), itemStack, null);
    }

    @Override
    protected List<AbstractSlot> getSlotInventories() {
        return Collections.emptyList();
    }

    @Override
    protected FastOfferResult offerFast(ItemStack stack) {
        return new FastOfferResult(stack.copy(), false);
    }

    @Override
    public boolean isValidItem(ItemStack stack) {
        return false;
    }

    @Override
    public IInventory intersect(Inventory inventory) {
        return this;
    }

    @Override
    public IInventory union(Inventory inventory) {
        return inventory instanceof EmptyInventory ? this : (IInventory) inventory;
    }

    @Override
    public boolean containsInventory(Inventory inventory) {
        return false;
    }

    @Override
    public <T extends Inventory> T first() {
        return genericEmpty();
    }

    @Override
    public <T extends Inventory> T next() {
        return genericEmpty();
    }

    @Override
    public Optional<ItemStack> poll() {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> poll(int limit) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> peek() {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> peek(int limit) {
        return Optional.empty();
    }

    @Override
    public InventoryTransactionResult offer(ItemStack stack) {
        return CachedInventoryTransactionResults.FAIL_NO_TRANSACTIONS;
    }

    @Override
    public InventoryTransactionResult setForced(@Nullable ItemStack stack) {
        return CachedInventoryTransactionResults.FAIL_NO_TRANSACTIONS;
    }

    @Override
    public InventoryTransactionResult set(@Nullable ItemStack stack) {
        return CachedInventoryTransactionResults.FAIL_NO_TRANSACTIONS;
    }

    @Override
    public void clear() {
    }

    @Override
    public int size() {
        return 0;
    }

    @Override
    public int totalItems() {
        return 0;
    }

    @Override
    public int capacity() {
        return 0;
    }

    @Override
    public boolean hasChildren() {
        return false;
    }

    @Override
    public boolean contains(ItemStack stack) {
        return false;
    }

    @Override
    public boolean contains(ItemType type) {
        return false;
    }

    @Override
    public boolean containsAny(ItemStack stack) {
        return false;
    }

    @Override
    public int getMaxStackSize() {
        return 0;
    }

    @Override
    public void setMaxStackSize(int size) {
    }

    @Override
    protected <T extends Inventory> T queryInventories(Predicate<AbstractMutableInventory> predicate) {
        return genericEmpty();
    }

    @Override
    public Iterator<Inventory> iterator() {
        return Collections.emptyIterator();
    }

    @Override
    public PluginContainer getPlugin() {
        // Use the plugin container from the parent if possible
        final AbstractInventory parent = parent();
        return parent == this ? Lantern.getMinecraftPlugin() : parent.getPlugin();
    }

    @Override
    public InventoryArchetype getArchetype() {
        return LanternInventoryArchetypes.EMPTY;
    }

    @Override
    public Translation getName() {
        return Name.INSTANCE;
    }
}
