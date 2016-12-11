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

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.util.collect.EmptyIterator;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.translation.Translation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

/**
 * Bottom type / empty results set for inventory queries.
 */
class LanternEmptyInventory extends AbstractInventory implements EmptyInventory {

    @Nullable private final AbstractInventory parent;

    LanternEmptyInventory(@Nullable Inventory parent) {
        this.parent = (AbstractInventory) parent;
    }

    @Override
    protected LanternEmptyInventory empty() {
        return this;
    }

    @Override
    public AbstractInventory parent() {
        return this.parent == null ? this : this.parent;
    }

    @Override
    public <T extends Inventory> Iterable<T> slots() {
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T first() {
        return (T) this;
    }

    @Override
    public Optional<ItemStack> poll(Predicate<ItemStack> matcher) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> poll(int limit, Predicate<ItemStack> matcher) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> peek(Predicate<ItemStack> matcher) {
        return Optional.empty();
    }

    @Override
    public Optional<PeekPollTransactionsResult> peekPollTransactions(Predicate<ItemStack> matcher) {
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> peek(int limit, Predicate<ItemStack> matcher) {
        return Optional.empty();
    }

    @Override
    public Optional<PeekPollTransactionsResult> peekPollTransactions(int limit, Predicate<ItemStack> matcher) {
        return Optional.empty();
    }

    @Override
    public PeekSetTransactionsResult peekSetTransactions(@Nullable ItemStack stack) {
        return new PeekSetTransactionsResult(new ArrayList<>(), InventoryTransactionResult.builder()
                .type(InventoryTransactionResult.Type.FAILURE).reject(stack).build());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(Predicate<Inventory> matcher, boolean nested) {
        return (T) this;
    }

    @Override
    public boolean isValidItem(ItemStack stack) {
        return false;
    }

    @Override
    public boolean isChild(Inventory child) {
        return false;
    }

    @Override
    public int slotCount() {
        return 0;
    }

    @Override
    public void add(ContainerViewListener listener) {
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
    public int getMaxStackSize() {
        return 0;
    }

    @Override
    public void setMaxStackSize(int size) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(Class<?>... types) {
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(ItemType... types) {
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(ItemStack... types) {
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(InventoryProperty<?, ?>... props) {
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(Translation... names) {
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(String... names) {
        return (T) this;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(Object... args) {
        return (T) this;
    }

    @Override
    public PluginContainer getPlugin() {
        return Lantern.getMinecraftPlugin();
    }

    @Override
    public InventoryArchetype getArchetype() {
        return LanternInventoryArchetypes.EMPTY;
    }

    @Override
    public Iterator<Inventory> iterator() {
        return EmptyIterator.get();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T next() {
        return (T) this;
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
    public boolean hasProperty(InventoryProperty<?, ?> property) {
        return false;
    }

    @Override
    public boolean hasProperty(Inventory child, InventoryProperty<?, ?> property) {
        return false;
    }

    @Override
    public FastOfferResult offerFast(ItemStack stack) {
        return new FastOfferResult(checkNotNull(stack, "stack"), false);
    }

    @Override
    public PeekOfferTransactionsResult peekOfferFastTransactions(ItemStack stack) {
        return new PeekOfferTransactionsResult(new ArrayList<>(), new FastOfferResult(checkNotNull(stack, "stack"), false));
    }

    @Override
    public InventoryTransactionResult offer(ItemStack stack) {
        return InventoryTransactionResult.builder().type(InventoryTransactionResult.Type.FAILURE).reject(stack).build();
    }

    @Override
    public InventoryTransactionResult set(ItemStack stack) {
        return InventoryTransactionResult.builder().type(InventoryTransactionResult.Type.FAILURE).reject(stack).build();
    }

    @Override
    public Translation getName() {
        return AbstractMutableInventory.NameHolder.EMPTY;
    }
}
