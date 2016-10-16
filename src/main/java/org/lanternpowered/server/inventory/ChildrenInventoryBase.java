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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Iterables;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.translation.Translation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

public class ChildrenInventoryBase extends InventoryBase {

    final List<InventoryBase> children;

    public ChildrenInventoryBase(@Nullable Inventory parent, @Nullable Translation name) {
        this(parent, name, new ArrayList<>());
    }

    public ChildrenInventoryBase(@Nullable Inventory parent, @Nullable Translation name, List<InventoryBase> children) {
        super(parent, name);
        this.children = children;
    }

    /**
     * Gets the children of the inventory.
     *
     * @return The children
     */
    public List<InventoryBase> getChildren() {
        return Collections.unmodifiableList(this.children);
    }

    /**
     * Registers a child {@link Inventory} for this inventory. {@link Slot}s
     * cannot be added through this method.
     *
     * @param childInventory The child inventory
     */
    protected <T extends Inventory> T registerChild(T childInventory) {
        checkNotNull(childInventory, "childInventory");
        checkArgument(!this.children.contains(childInventory), "The child is already registered");
        this.children.add((InventoryBase) childInventory);
        return childInventory;
    }

    Iterable<LanternSlot> getSlotInventories() {
        return Collections.emptyList();
    }

    /**
     * Prioritizes a child {@link Inventory} to have a higher priority
     * for {@link Inventory#poll}, {@link Inventory#offer(ItemStack)},
     * ... functions. The child must be registered
     *
     * @param childInventory The child inventory
     * @param <T> The child inventory type
     * @return The child inventory
     */
    protected <T extends Inventory> T prioritizeChild(T childInventory) {
        checkNotNull(childInventory, "inventory");
        checkArgument(this.children.contains(childInventory), "The inventory is not registered");
        if (this.children.size() == 1) {
            return childInventory;
        }
        this.children.remove(childInventory);
        this.children.add(0, (InventoryBase) childInventory);
        return childInventory;
    }

    private FastOfferResult offerFast(ItemStack stack, List<Inventory> processed, boolean add) {
        FastOfferResult offerResult = null;
        // Loop through the slots
        for (InventoryBase inventory : this.children) {
            if (!add && processed.contains(inventory)) {
                continue;
            }
            offerResult = inventory.offerFast(stack);
            if (offerResult.getRest() == null) {
                return offerResult;
            }
            stack = offerResult.getRest();
            if (add) {
                processed.add(inventory);
            }
        }
        if (offerResult == null) {
            return new FastOfferResult(stack, false);
        }
        return offerResult;
    }

    @Override
    public FastOfferResult offerFast(ItemStack stack) {
        checkNotNull(stack, "stack");
        FastOfferResult offerResult = null;
        List<Inventory> processed = new ArrayList<>();
        Inventory inventory = this.query(stack);
        if (inventory instanceof ChildrenInventoryBase) {
            offerResult = ((ChildrenInventoryBase) inventory).offerFast(stack, processed, true);
        }
        if (offerResult != null && (stack = offerResult.getRest()) == null) {
            return offerResult;
        }
        offerResult = this.offerFast(stack, processed, false);
        return offerResult;
    }

    private PeekOfferTransactionsResult peekOfferFastTransactions(ItemStack stack, List<Inventory> processed, boolean add) {
        PeekOfferTransactionsResult peekResult = null;
        List<SlotTransaction> transactions = new ArrayList<>();
        // Loop through the slots
        for (InventoryBase inventory : this.children) {
            if (!add && processed.contains(inventory)) {
                continue;
            }
            peekResult = inventory.peekOfferFastTransactions(stack);
            if (peekResult.getOfferResult().getRest() == null) {
                peekResult.getTransactions().addAll(transactions);
                return peekResult;
            } else {
                transactions.addAll(peekResult.getTransactions());
            }
            stack = peekResult.getOfferResult().getRest();
            if (add) {
                processed.add(inventory);
            }
        }
        if (peekResult == null) {
            return new PeekOfferTransactionsResult(transactions, new FastOfferResult(stack, false));
        }
        return new PeekOfferTransactionsResult(transactions, peekResult.getOfferResult());
    }

    @Override
    public PeekOfferTransactionsResult peekOfferFastTransactions(ItemStack stack) {
        checkNotNull(stack, "stack");
        PeekOfferTransactionsResult peekResult = null;
        List<Inventory> processed = new ArrayList<>();
        Inventory inventory = this.query(stack);
        if (inventory instanceof ChildrenInventoryBase) {
            peekResult = ((ChildrenInventoryBase) inventory).peekOfferFastTransactions(stack, processed, true);
        }
        if (peekResult != null && (stack = peekResult.getOfferResult().getRest()) == null) {
            return peekResult;
        }
        PeekOfferTransactionsResult peekResult1 = this.peekOfferFastTransactions(stack, processed, false);
        if (peekResult != null) {
            peekResult1.getTransactions().addAll(peekResult.getTransactions());
        }
        return peekResult1;
    }

    List<Inventory> queryInventories(Predicate<Inventory> matcher, boolean nested) {
        int count = 0;
        final List<Inventory> matches = new ArrayList<>();
        for (Inventory inventory : this.children) {
            count++;
            if (matcher.test(inventory)) {
                matches.add(inventory);
            }
            if (nested) {
                Inventory inventory1 = ((InventoryBase) inventory).query(matcher, true);
                if (!(inventory1 instanceof EmptyInventory)) {
                    matches.add(inventory1);
                }
            } else if (inventory instanceof ChildrenInventoryBase) {
                matches.addAll(((ChildrenInventoryBase) inventory).queryInventories(matcher, false));
            }
        }
        // All the children were a match, we will return this
        if (!nested && matches.size() == count) {
            return Collections.singletonList(this);
        }
        return matches;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(Predicate<Inventory> matcher, boolean nested) {
        checkNotNull(matcher, "matcher");
        final List<Inventory> matches = queryInventories(matcher, nested);
        if (matches.isEmpty()) {
            return (T) this.emptyInventory;
        }
        return (T) new ChildrenInventoryBase(null, null, Collections.unmodifiableList((List) matches));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> Iterable<T> slots() {
        return (Iterable) Iterables.unmodifiableIterable(this.getSlotInventories());
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T first() {
        final Iterator<Inventory> it = this.iterator();
        return (T) (it.hasNext() ? it.next() : this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T next() {
        // TODO
        return (T) this.emptyInventory;
    }

    @Override
    public InventoryTransactionResult set(ItemStack stack) {
        return InventoryTransactionResult.builder().type(InventoryTransactionResult.Type.FAILURE).reject(stack).build();
    }

    @Override
    public void clear() {
        // Clear all the sub inventories
        this.iterator().forEachRemaining(Inventory::clear);
    }

    @Override
    public int size() {
        int size = 0;
        for (Inventory inventory : this.children) {
            size += inventory.size();
        }
        return size;
    }

    @Override
    public int totalItems() {
        int totalItems = 0;
        for (Inventory inventory : this.children) {
            totalItems += inventory.totalItems();
        }
        return totalItems;
    }

    @Override
    public int capacity() {
        int capacity = 0;
        for (Inventory inventory : this.children) {
            capacity += inventory.capacity();
        }
        return capacity;
    }

    @Override
    public boolean hasChildren() {
        return !this.children.isEmpty();
    }

    @Override
    public int getMaxStackSize() {
        return 64;
    }

    @Override
    public void setMaxStackSize(int size) {
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<Inventory> iterator() {
        return (Iterator) this.children.iterator();
    }

    @Override
    public boolean contains(ItemStack stack) {
        checkNotNull(stack, "stack");
        // Loop through the inventories
        for (InventoryBase inventory : this.children) {
            if (inventory.contains(stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean contains(ItemType type) {
        checkNotNull(type, "type");
        // Loop through the inventories
        for (InventoryBase inventory : this.children) {
            if (inventory.contains(type)) {
                return true;
            }
        }
        return false;
    }

    private static class ItemMatcher implements Predicate<ItemStack> {

        private final ItemStack itemStack;

        public ItemMatcher(ItemStack itemStack) {
            this.itemStack = itemStack;
        }

        @Override
        public boolean test(ItemStack itemStack) {
            return ((LanternItemStack) this.itemStack).isEqualToOther(itemStack);
        }
    }

    @Override
    public Optional<ItemStack> poll(Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        // Loop through the children inventories
        for (InventoryBase inventory : this.children) {
            final Optional<ItemStack> itemStack = inventory.poll(matcher);
            if (itemStack.isPresent()) {
                return itemStack;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> poll(int limit, Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        checkArgument(limit >= 0, "Limit may not be negative");
        if (limit == 0) {
            return Optional.empty();
        }
        ItemStack stack = null;
        // Loop through the children inventories
        for (InventoryBase inventory : this.children) {
            // Check whether the slot a item contains
            if (stack == null) {
                stack = inventory.poll(limit, matcher).orElse(null);
                if (stack != null) {
                    if (stack.getQuantity() >= limit) {
                        return Optional.of(stack);
                    } else {
                        limit -= stack.getQuantity();
                        if (!(matcher instanceof ItemMatcher)) {
                            matcher = new ItemMatcher(stack);
                        }
                    }
                }
            } else {
                final Optional<ItemStack> optItemStack = inventory.poll(limit, matcher);
                if (optItemStack.isPresent()) {
                    final int stackSize = optItemStack.get().getQuantity();
                    limit -= stackSize;
                    stack.setQuantity(stack.getQuantity() + stackSize);
                    if (limit <= 0) {
                        return Optional.of(stack);
                    }
                }
            }
        }
        return Optional.ofNullable(stack);
    }

    @Override
    public Optional<ItemStack> peek(Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        // Loop through the children inventories
        for (InventoryBase inventory : this.children) {
            Optional<ItemStack> itemStack = inventory.peek(matcher);
            if (itemStack.isPresent()) {
                return itemStack;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<PeekPollTransactionsResult> peekPollTransactions(Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        // Loop through the children inventories
        for (InventoryBase inventory : this.children) {
            Optional<PeekPollTransactionsResult> peekResult = inventory.peekPollTransactions(matcher);
            if (peekResult.isPresent()) {
                return peekResult;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ItemStack> peek(int limit, Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        checkArgument(limit >= 0, "Limit may not be negative");
        if (limit == 0) {
            return Optional.empty();
        }
        ItemStack stack = null;
        // Loop through the children inventories
        for (InventoryBase inventory : this.children) {
            // Check whether the slot a item contains
            if (stack == null) {
                stack = inventory.peek(limit, matcher).orElse(null);
                if (stack != null) {
                    if (stack.getQuantity() >= limit) {
                        return Optional.of(stack);
                    } else {
                        limit -= stack.getQuantity();
                        if (!(matcher instanceof ItemMatcher)) {
                            matcher = new ItemMatcher(stack);
                        }
                    }
                }
            } else {
                int peekedStackSize = 0;
                // Check whether the inventory a slot is to avoid
                // boxing/unboxing and cloning the item stack
                if (inventory instanceof Slot) {
                    final ItemStack stack1 = ((LanternSlot) inventory).getRawItemStack();
                    if (stack1 != null && matcher.test(stack1)) {
                        peekedStackSize = Math.min(((Slot) inventory).getStackSize(), limit);
                    }
                } else {
                    final Optional<ItemStack> optItemStack = inventory.peek(limit, matcher);
                    if (optItemStack.isPresent()) {
                        peekedStackSize = optItemStack.get().getQuantity();
                    }
                }

                if (peekedStackSize > 0) {
                    limit -= peekedStackSize;
                    stack.setQuantity(stack.getQuantity() + peekedStackSize);
                    if (limit <= 0) {
                        return Optional.of(stack);
                    }
                }
            }
        }
        return Optional.ofNullable(stack);
    }

    @Override
    public Optional<PeekPollTransactionsResult> peekPollTransactions(int limit, Predicate<ItemStack> matcher) {
        checkNotNull(matcher, "matcher");
        checkArgument(limit >= 0, "Limit may not be negative");
        if (limit == 0) {
            return Optional.empty();
        }
        PeekPollTransactionsResult peekResult = null;
        // Loop through the children inventories
        for (InventoryBase inventory : this.children) {
            // Check whether the slot a item contains
            if (peekResult == null) {
                peekResult = inventory.peekPollTransactions(limit, matcher).orElse(null);
                if (peekResult != null) {
                    if (peekResult.getPeekedItem().getQuantity() >= limit) {
                        return Optional.of(peekResult);
                    } else {
                        limit -= peekResult.getPeekedItem().getQuantity();
                        if (!(matcher instanceof ItemMatcher)) {
                            matcher = new ItemMatcher(peekResult.getPeekedItem());
                        }
                    }
                }
            } else {
                final PeekPollTransactionsResult peekResult1 = inventory.peekPollTransactions(limit, matcher).orElse(null);
                if (peekResult1 != null) {
                    final int peekedStackSize = peekResult1.getPeekedItem().getQuantity();
                    final ItemStack peekedItem = peekResult.getPeekedItem();
                    limit -= peekedStackSize;
                    peekedItem.setQuantity(peekedItem.getQuantity() + peekedStackSize);
                    peekResult.getTransactions().addAll(peekResult1.getTransactions());
                    if (limit <= 0) {
                        return Optional.of(peekResult);
                    }
                }
            }
        }
        return Optional.ofNullable(peekResult);
    }

    @Override
    public PeekSetTransactionsResult peekSetTransactions(@Nullable ItemStack stack) {
        return new PeekSetTransactionsResult(new ArrayList<>(), InventoryTransactionResult.builder()
                .type(InventoryTransactionResult.Type.FAILURE).reject(stack).build());
    }

    @Override
    public boolean isValidItem(ItemStack stack) {
        checkNotNull(stack, "stack");
        for (InventoryBase child : this.children) {
            if (child.isValidItem(stack)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public boolean isChild(Inventory child) {
        checkNotNull(child, "child");
        for (InventoryBase child0 : this.children) {
            if (child0 == child) {
                return true;
            }
            if (child0.isChild(child)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public int slotCount() {
        int slotCount = 0;
        for (InventoryBase child : this.children) {
            if (child instanceof Slot) {
                slotCount++;
            } else {
                slotCount += child.slotCount();
            }
        }
        return slotCount;
    }

    @Override
    protected void addViewer(Viewer viewer, LanternContainer container) {
        super.addViewer(viewer, container);
        this.children.forEach(child -> child.addViewer(viewer, container));
    }

    @Override
    protected void removeViewer(Viewer viewer, LanternContainer container) {
        super.removeViewer(viewer, container);
        this.children.forEach(child -> child.removeViewer(viewer, container));
    }
}
