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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.Iterables;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.text.translation.Translation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

public class ChildrenInventoryBase extends InventoryBase {

    /**
     * All the children {@link Inventory}s of this inventory, there shouldn't be any {@link Slot}s
     * inside this list.
     */
    private final List<InventoryBase> children = new ArrayList<>();

    public ChildrenInventoryBase(@Nullable Inventory parent, @Nullable Translation name) {
        super(parent, name);
    }

    @SuppressWarnings("unchecked")
    protected void registerChildren(Collection<Inventory> childInventories) {
        this.children.addAll((Collection) checkNotNull(childInventories, "childInventories"));
    }

    /**
     * Registers a child {@link Inventory} for this inventory. {@link Slot}s
     * cannot be added through this method.
     *
     * @param childInventory The child inventory
     */
    protected <T extends Inventory> T registerChild(T childInventory) {
        checkNotNull(childInventory, "childInventory");
        this.children.add((InventoryBase) childInventory);
        return childInventory;
    }

    Iterable<LanternSlot> getSlotInventories() {
        return Collections.emptyList();
    }

    @Override
    public FastOfferResult offerFast(ItemStack stack) {
        checkNotNull(stack, "stack");
        FastOfferResult offerResult = null;
        // Loop through the slots
        for (Inventory inventory : this.children) {
            offerResult = ((InventoryBase) inventory).offerFast(stack);
            if (offerResult.getRest() == null) {
                return offerResult;
            }
            stack = offerResult.getRest();
        }
        if (offerResult == null) {
            return new FastOfferResult(stack, false);
        }
        return offerResult;
    }

    List<Inventory> queryInventories(Predicate<Inventory> matcher) {
        int count = 0;
        final List<Inventory> matches = new ArrayList<>();
        for (Inventory inventory : this.children) {
            count++;
            if (matcher.test(inventory)) {
                matches.add(inventory);
            }
        }
        // All the children were a match, we will return this
        if (matches.size() == count) {
            return Collections.singletonList(this);
        }
        return matches;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(Predicate<Inventory> matcher) {
        checkNotNull(matcher, "matcher");
        final List<Inventory> matches = queryInventories(matcher);
        if (matches.isEmpty()) {
            return (T) this.emptyInventory;
        } else if (matches.size() == 1) {
            return (T) matches.get(0);
        }
        final ChildrenInventoryBase childrenInventory = new ChildrenInventoryBase(null, null) {
            {
                this.registerChildren(matches);
                this.finalizeContent();
            }
        };
        return (T) childrenInventory;
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
    public boolean isEmpty() {
        return this.children.isEmpty();
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
            if (itemStack.isPresent() && matcher.test(itemStack.get())) {
                return itemStack;
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

}
