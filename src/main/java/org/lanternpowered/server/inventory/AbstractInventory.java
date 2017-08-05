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
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.lanternpowered.server.util.collect.EmptyIterator;
import org.spongepowered.api.effect.Viewer;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.AbstractInventoryProperty;
import org.spongepowered.api.item.inventory.property.InventoryCapacity;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.transaction.SlotTransaction;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

@SuppressWarnings("unchecked")
public abstract class AbstractInventory implements IInventory {

    protected abstract LanternEmptyInventory empty();

    AbstractInventory getChild(int index) {
        return empty();
    }

    int getChildIndex(AbstractInventory inventory) {
        return -1;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Iterator<Inventory> iterator() {
        return EmptyIterator.get();
    }

    @Override
    public abstract AbstractInventory parent();

    @Override
    public void addViewListener(ContainerViewListener listener) {
    }

    /**
     * Offers the {@link ItemStack} fast to this inventory, avoiding
     * the creation of {@link InventoryTransactionResult}s.
     *
     * @param stack The item stack
     * @return The fast offer result
     */
    protected abstract FastOfferResult offerFast(ItemStack stack);

    public abstract PeekOfferTransactionsResult peekOfferFastTransactions(ItemStack stack);

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T first() {
        final Inventory inventory = getChild(0);
        return inventory instanceof EmptyInventory ? (T) this : (T) inventory;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T next() {
        final AbstractInventory parent = parent();
        if (parent == this) {
            return (T) empty();
        }
        final int index = parent.getChildIndex(this);
        checkState(index != -1);
        return (T) parent.getChild(index + 1);
    }

    @Override
    public Optional<ItemStack> poll() {
        return poll(stack -> true);
    }

    @Override
    public Optional<ItemStack> poll(ItemType itemType) {
        checkNotNull(itemType, "itemType");
        return poll(stack -> stack.getType().equals(itemType));
    }

    @Override
    public Optional<ItemStack> poll(int limit) {
        return poll(limit, stack -> true);
    }

    @Override
    public Optional<ItemStack> poll(int limit, ItemType itemType) {
        checkNotNull(itemType, "itemType");
        return poll(limit, stack -> stack.getType().equals(itemType));
    }

    @Override
    public Optional<ItemStack> peek() {
        return peek(stack -> true);
    }

    @Override
    public Optional<ItemStack> peek(ItemType itemType) {
        checkNotNull(itemType, "itemType");
        return peek(stack -> stack.getType().equals(itemType));
    }

    @Override
    public Optional<ItemStack> peek(int limit) {
        return peek(limit, stack -> true);
    }

    @Override
    public Optional<ItemStack> peek(int limit, ItemType itemType) {
        checkNotNull(itemType, "itemType");
        return peek(limit, stack -> stack.getType().equals(itemType));
    }

    protected abstract Optional<PeekPollTransactionsResult> peekPollTransactions(Predicate<ItemStack> matcher);

    public abstract Optional<PeekPollTransactionsResult> peekPollTransactions(int limit, Predicate<ItemStack> matcher);

    /**
     * Peeks for the result {@link SlotTransaction}s and {@link InventoryTransactionResult}
     * that would occur if you try to set a item through {@link Inventory#set(ItemStack)}.
     *
     * @param itemStack The item stack to set
     * @return The peeked transaction results
     */
    protected abstract PeekSetTransactionsResult peekSetTransactions(@Nullable ItemStack itemStack);

    @Override
    public IInventory union(Inventory inventory) {
        checkNotNull(inventory, "inventory");
        return new LanternOrderedInventory(this, null) {
            {
                // Add all the slots from this inventory
                AbstractInventory.this.<LanternSlot>slots().forEach(this::registerSlot);
                // Add all the slots from the other inventory, if not already added by the previous inventory
                inventory.<LanternSlot>slots().forEach(slot -> {
                    if (!this.slots.contains(slot)) {
                        registerSlot(slot);
                    }
                });
                finalizeContent();
            }
        };
    }

    @Override
    public boolean hasProperty(Class<? extends InventoryProperty<?, ?>> property) {
        checkNotNull(property, "property");
        final AbstractInventory parent = parent();
        Optional<InventoryProperty<?, ?>> optProperty = tryGetProperty((Class) property, null);
        if (parent != this && !optProperty.isPresent()) {
            optProperty = parent.tryGetProperty(
                    this, (Class) property, null);
        }
        return optProperty.isPresent();
    }

    @Override
    public boolean hasProperty(InventoryProperty<?, ?> property) {
        checkNotNull(property, "property");
        final AbstractInventory parent = parent();
        Optional<InventoryProperty<?, ?>> optProperty = tryGetProperty((Class) property.getClass(), property.getKey());
        if (parent != this && !optProperty.isPresent()) {
            optProperty = parent.tryGetProperty(
                    this, (Class) property.getClass(), property.getKey());
        }
        return optProperty.isPresent() && optProperty.get().equals(property);
    }

    @Override
    public boolean hasProperty(Inventory child, InventoryProperty<?,?> property) {
        checkNotNull(property, "property");
        Optional<InventoryProperty<?, ?>> optProperty = tryGetProperty(
                child, (Class) property.getClass(), property.getKey());
        if (!optProperty.isPresent()) {
            optProperty = ((AbstractInventory) child).tryGetProperty((Class) property.getClass(), property.getKey());
        }
        return optProperty.isPresent() && optProperty.get().equals(property);
    }

    @Override
    public final <T extends InventoryProperty<?, ?>> Collection<T> getProperties(Class<T> property) {
        return getPropertiesBuilder(property).build();
    }

    <T extends InventoryProperty<?, ?>> ImmutableList.Builder<T> getPropertiesBuilder(Class<T> property) {
        checkNotNull(property, "property");
        final AbstractInventory parent = parent();
        final ImmutableList.Builder<T> properties = ImmutableList.builder();
        properties.addAll(tryGetProperties(property));
        if (parent != this) {
            properties.addAll(parent.tryGetProperties(this, property));
        }
        return properties;
    }

    @Override
    public final <T extends InventoryProperty<?, ?>> Collection<T> getProperties(Inventory child, Class<T> property) {
        return getPropertiesBuilder(child, property).build();
    }

    <T extends InventoryProperty<?, ?>> ImmutableList.Builder<T> getPropertiesBuilder(Inventory child, Class<T> property) {
        checkNotNull(child, "child");
        checkNotNull(property, "property");
        final ImmutableList.Builder<T> properties = ImmutableList.builder();
        properties.addAll(tryGetProperties(child, property));
        properties.addAll(((AbstractInventory) child).tryGetProperties(property));
        return properties;
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Optional<T> getInventoryProperty(Class<T> property) {
        return getProperty(property, AbstractInventoryProperty.getDefaultKey(property));
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Optional<T> getProperty(Class<T> property, @Nullable Object key) {
        checkNotNull(property, "property");
        final AbstractInventory parent = parent();
        if (parent != this) {
            final Optional<T> optProperty = parent.tryGetProperty(this, property, key);
            if (optProperty.isPresent()) {
                return optProperty;
            }
        }
        return tryGetProperty(property, key);
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Optional<T> getInventoryProperty(Inventory child, Class<T> property) {
        return getProperty(child, property, AbstractInventoryProperty.getDefaultKey(property));
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Optional<T> getProperty(Inventory child, Class<T> property, @Nullable Object key) {
        checkNotNull(child, "child");
        checkNotNull(property, "property");
        Optional<T> optProperty = tryGetProperty(child, property, key);
        if (!optProperty.isPresent()) {
            optProperty = ((AbstractInventory) child).tryGetProperty(property, key);
        }
        return optProperty;
    }

    protected <T extends InventoryProperty<?, ?>> Optional<T> tryGetProperty(Class<T> property, @Nullable Object key) {
        if (property == InventoryTitle.class) {
            return Optional.of((T) new InventoryTitle(Text.of(getName())));
        } else if (property == InventoryCapacity.class) {
            return Optional.of((T) new InventoryCapacity(capacity()));
        }
        return Optional.empty();
    }

    protected <T extends InventoryProperty<?, ?>> List<T> tryGetProperties(Class<T> property) {
        final List<T> properties = new ArrayList<>();
        if (property == InventoryTitle.class) {
            properties.add((T) new InventoryTitle(Text.of(getName())));
        } else if (property == InventoryCapacity.class) {
            properties.add((T) new InventoryCapacity(capacity()));
        }
        return properties;
    }

    protected <T extends InventoryProperty<?, ?>> Optional<T> tryGetProperty(Inventory child, Class<T> property, @Nullable Object key) {
        return Optional.empty();
    }

    protected <T extends InventoryProperty<?, ?>> List<T> tryGetProperties(Inventory child, Class<T> property) {
        return new ArrayList<>();
    }

    void addViewer(Viewer viewer, LanternContainer container) {
    }

    void removeViewer(Viewer viewer, LanternContainer container) {
    }

    void close() {
    }
}
