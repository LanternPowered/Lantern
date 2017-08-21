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

import org.lanternpowered.server.inventory.slot.SlotChangeListener;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetype;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.text.translation.Translation;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public class AltParentProxyInventory extends AbstractInventory {

    private final LanternEmptyInventory emptyInventory = new LanternEmptyInventory(this);
    protected final AbstractInventory delegate;
    @Nullable private final AbstractInventory parent;

    protected AltParentProxyInventory(@Nullable Inventory parent, Inventory delegate) {
        this.delegate = (AbstractInventory) checkNotNull(delegate, "delegate");
        this.parent = (AbstractInventory) parent;
    }

    @Override
    protected LanternEmptyInventory empty() {
        return this.emptyInventory;
    }

    @Override
    AbstractInventory getChild(int index) {
        return AltParentProxyInventories.get(this, this.delegate.getChild(index));
    }

    @Override
    int getChildIndex(AbstractInventory inventory) {
        return this.delegate.getChildIndex(AltParentProxyInventories.getOriginal(inventory));
    }

    @Override
    public AbstractInventory parent() {
        return this.parent == null ? this : this.parent;
    }

    @Override
    public void addChangeListener(SlotChangeListener listener) {
        this.delegate.addChangeListener(listener);
    }

    @Override
    public void addCloseListener(InventoryCloseListener listener) {
        this.delegate.addCloseListener(listener);
    }

    @Override
    public Iterator<Inventory> iterator() {
        return new Iterator<Inventory>() {
            private final Iterator<Inventory> it = delegate.iterator();

            @Override
            public boolean hasNext() {
                return this.it.hasNext();
            }

            @Override
            public Inventory next() {
                return AltParentProxyInventories.get(AltParentProxyInventory.this, this.it.next());
            }

            @Override
            public void remove() {
                this.it.remove();
            }
        };
    }

    @Override
    public <T extends Inventory> Iterable<T> slots() {
        return () -> new Iterator<T>() {
            private final Iterator<T> it = (Iterator<T>) delegate.slots().iterator();

            @Override
            public boolean hasNext() {
                return this.it.hasNext();
            }

            @Override
            public T next() {
                final T next = this.it.next();
                Inventory inventory = next.parent();
                if (next == inventory || inventory == delegate) {
                    return (T) AltParentProxyInventories.get(AltParentProxyInventory.this, next);
                }
                final List<Inventory> stack = new ArrayList<>();
                stack.add(next);
                stack.add(inventory);
                while (inventory != delegate) {
                    final Inventory inventory1 = inventory.parent();
                    stack.add(inventory1);
                    if (inventory1 == inventory) {
                        throw new IllegalStateException();
                    }
                    inventory = inventory1;
                }
                for (int i = 0; i < stack.size() - 1; i++) {
                    stack.set(i, AltParentProxyInventories.get(stack.get(i + 1), stack.get(i)));
                }
                return (T) stack.get(stack.size() - 1);
            }

            @Override
            public void remove() {
                this.it.remove();
            }
        };
    }

    @Override
    public <T extends Inventory> T first() {
        return (T) AltParentProxyInventories.get(this, this.delegate.first());
    }

    @Override
    public <T extends Inventory> T next() {
        return (T) AltParentProxyInventories.get(this, this.delegate.next());
    }

    @Override
    public Optional<ItemStack> poll(Predicate<ItemStack> matcher) {
        return this.delegate.poll(matcher);
    }

    @Override
    public Optional<ItemStack> poll(int limit, Predicate<ItemStack> matcher) {
        return this.delegate.poll(limit, matcher);
    }

    @Override
    public Optional<ItemStack> peek(Predicate<ItemStack> matcher) {
        return this.delegate.peek(matcher);
    }

    @Override
    public Optional<ItemStack> peek(int limit, Predicate<ItemStack> matcher) {
        return this.delegate.peek(limit, matcher);
    }

    @Override
    public <T extends Inventory> T query(Predicate<Inventory> matcher, boolean nested) {
        return (T) AltParentProxyInventories.get(this, this.delegate.query(matcher, nested));
    }

    @Override
    public boolean isValidItem(ItemStack stack) {
        return this.delegate.isValidItem(stack);
    }

    @Override
    public boolean isChild(Inventory child) {
        return this.delegate.isChild(child);
    }

    @Override
    public int slotCount() {
        return this.delegate.slotCount();
    }

    @Override
    public InventoryTransactionResult offer(ItemStack stack) {
        return this.delegate.offer(stack);
    }

    @Override
    public InventoryTransactionResult set(ItemStack stack) {
        return this.delegate.set(stack);
    }

    @Override
    public void clear() {
        this.delegate.clear();
    }

    @Override
    public int size() {
        return this.delegate.size();
    }

    @Override
    public int totalItems() {
        return this.delegate.totalItems();
    }

    @Override
    public int capacity() {
        return this.delegate.capacity();
    }

    @Override
    public boolean hasChildren() {
        return this.delegate.hasChildren();
    }

    @Override
    public boolean contains(ItemStack stack) {
        return this.delegate.contains(stack);
    }

    @Override
    public boolean containsAny(ItemStack stack) {
        return this.delegate.containsAny(stack);
    }

    @Override
    public boolean contains(ItemType type) {
        return this.delegate.contains(type);
    }

    @Override
    public int getMaxStackSize() {
        return this.delegate.getMaxStackSize();
    }

    @Override
    public void setMaxStackSize(int size) {
        this.delegate.setMaxStackSize(size);
    }

    @Override
    public <T extends Inventory> T query(Class<?>... types) {
        return (T) AltParentProxyInventories.get(this, this.delegate.query(types));
    }

    @Override
    public <T extends Inventory> T query(ItemType... types) {
        return (T) AltParentProxyInventories.get(this, this.delegate.query(types));
    }

    @Override
    public <T extends Inventory> T query(ItemStack... types) {
        return (T) AltParentProxyInventories.get(this, this.delegate.query(types));
    }

    @Override
    public <T extends Inventory> T queryAny(ItemStack... types) {
        return (T) AltParentProxyInventories.get(this, this.delegate.queryAny(types));
    }

    @Override
    public <T extends Inventory> T query(InventoryProperty<?, ?>... props) {
        return (T) AltParentProxyInventories.get(this, this.delegate.query(props));
    }

    @Override
    public <T extends Inventory> T query(Translation... names) {
        return (T) AltParentProxyInventories.get(this, this.delegate.query(names));
    }

    @Override
    public <T extends Inventory> T query(String... names) {
        return (T) AltParentProxyInventories.get(this, this.delegate.query(names));
    }

    @Override
    public <T extends Inventory> T query(Object... args) {
        return (T) AltParentProxyInventories.get(this, this.delegate.query(args));
    }

    @Override
    public PluginContainer getPlugin() {
        return this.delegate.getPlugin();
    }

    @Override
    public InventoryArchetype getArchetype() {
        return this.delegate.getArchetype();
    }

    @Override
    public IInventory intersect(Inventory inventory) {
        return this.delegate.intersect(inventory);
    }

    @Override
    public IInventory union(Inventory inventory) {
        return this.delegate.union(inventory);
    }

    @Override
    public boolean containsInventory(Inventory inventory) {
        return this.delegate.containsInventory(inventory) || inventory == this;
    }

    @Override
    protected FastOfferResult offerFast(ItemStack stack) {
        return this.delegate.offerFast(stack);
    }

    @Override
    public PeekOfferTransactionsResult peekOfferFastTransactions(ItemStack stack) {
        return this.delegate.peekOfferFastTransactions(stack);
    }

    @Override
    protected Optional<PeekPollTransactionsResult> peekPollTransactions(Predicate<ItemStack> matcher) {
        return this.delegate.peekPollTransactions(matcher);
    }

    @Override
    public Optional<PeekPollTransactionsResult> peekPollTransactions(int limit, Predicate<ItemStack> matcher) {
        return this.delegate.peekPollTransactions(limit, matcher);
    }

    @Override
    protected PeekSetTransactionsResult peekSetTransactions(@Nullable ItemStack itemStack) {
        return this.delegate.peekSetTransactions(itemStack);
    }

    @Override
    public Translation getName() {
        return this.delegate.getName();
    }

    @Override
    void close() {
        this.delegate.close();
    }
}
