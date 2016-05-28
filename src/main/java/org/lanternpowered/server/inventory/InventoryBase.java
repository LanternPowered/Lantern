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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.text.translation.Translation;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;

import javax.annotation.Nullable;

public abstract class InventoryBase implements Inventory {

    private static class EmptyNameHolder {

        private static final Translation EMPTY_NAME = Lantern.getRegistry().getTranslationManager().get("inventory.empty.title");
    }

    @Nullable private final Inventory parent;
    @Nullable private final Translation name;

    /**
     * All the {@link InventoryProperty}s of this inventory mapped by their type.
     */
    private final Multimap<Class<?>, InventoryProperty<?,?>> inventoryPropertiesByClass = HashMultimap.create();

    /**
     * All the {@link InventoryProperty}s of this inventory mapped by their key.
     */
    private final Map<Object, InventoryProperty<?,?>> inventoryPropertiesByKey = new HashMap<>();

    protected final EmptyInventory emptyInventory = this instanceof EmptyInventory ?
            (EmptyInventory) this : new EmptyInventoryImpl(this);

    public InventoryBase(@Nullable Inventory parent, @Nullable Translation name) {
        this.parent = parent;
        this.name = name;
    }

    protected void finalizeContent() {
    }

    /**
     * Registers a {@link InventoryProperty} for this inventory.
     *
     * @param inventoryProperty The inventory property
     */
    public void registerProperty(InventoryProperty<?, ?> inventoryProperty) {
        checkNotNull(inventoryProperty, "inventoryProperty");
        this.inventoryPropertiesByClass.put(inventoryProperty.getClass(), inventoryProperty);
        this.inventoryPropertiesByKey.put(inventoryProperty.getKey(), inventoryProperty);
    }

    /**
     * Offers the {@link ItemStack} fast to this inventory, avoiding
     * the creation of {@link InventoryTransactionResult}s.
     *
     * @param stack The item stack
     * @return The fast offer result
     */
    public abstract FastOfferResult offerFast(ItemStack stack);

    public abstract PeekOfferTransactionsResult peekOfferFastTransactions(ItemStack stack);

    @Override
    public InventoryTransactionResult offer(ItemStack stack) {
        return this.offerFast(stack).asTransactionResult();
    }

    @Override
    public Inventory parent() {
        return this.parent == null ? this : this.parent;
    }

    @Override
    public Translation getName() {
        return this.name == null ? EmptyNameHolder.EMPTY_NAME : this.name;
    }

    /**
     * Gets whether the specified {@link InventoryProperty} is present on this inventory.
     *
     * @param property The property
     * @return Is present
     */
    public boolean hasProperty(InventoryProperty<?,?> property) {
        return this.inventoryPropertiesByKey.containsValue(checkNotNull(property, "property"));
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Collection<T> getProperties(Inventory child, Class<T> property) {
        // TODO: Get properties based on the parent, like SlotIndex
        return child.getProperties(property);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends InventoryProperty<?, ?>> Collection<T> getProperties(Class<T> property) {
        checkNotNull(property, "property");
        return Collections.unmodifiableCollection((Collection<? extends T>) this.inventoryPropertiesByClass.get(property));
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Optional<T> getProperty(Inventory child, Class<T> property, Object key) {
        // TODO: Get properties based on the parent, like SlotIndex
        return child.getProperty(property, key);
    }

    @Override
    public <T extends InventoryProperty<?, ?>> Optional<T> getProperty(Class<T> property, Object key) {
        checkNotNull(property, "property");
        checkNotNull(key, "key");
        final InventoryProperty<?,?> inventoryProperty = this.inventoryPropertiesByKey.get(key);
        return inventoryProperty != null && property.isInstance(inventoryProperty) ?
                Optional.of(property.cast(inventoryProperty)) : Optional.empty();
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(ItemType... types) {
        checkNotNull(types, "types");
        return this.query(inventory -> {
            // Slots are leaf nodes so only check if they contain
            // the item
            if (inventory instanceof Slot) {
                for (ItemType type : types) {
                    if (inventory.contains(type)) {
                        return true;
                    }
                }
            }
            return false;
        }, true);
    }

    @Override
    public <T extends Inventory> T query(Class<?>... types) {
        checkNotNull(types, "types");
        return this.query(inventory -> {
            for (Class<?> type : types) {
                if (type.isInstance(inventory)) {
                    return true;
                }
            }
            return false;
        }, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(ItemStack... types) {
        checkNotNull(types, "types");
        return this.query(inventory -> {
            // Slots are leaf nodes so only check if they contain
            // the item
            if (inventory instanceof Slot) {
                for (ItemStack type : types) {
                    if (inventory.contains(type)) {
                        return true;
                    }
                }
            }
            return false;
        }, true);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(InventoryProperty<?, ?>... props) {
        checkNotNull(props, "props");
        return this.query(inventory -> {
            for (InventoryProperty<?,?> prop : props) {
                if (((InventoryBase) inventory).hasProperty(prop)) {
                    return true;
                }
            }
            return false;
        }, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(Translation... names) {
        checkNotNull(names, "names");
        return this.query(inventory -> {
            for (Translation name : names) {
                if (inventory.getName().equals(name)) {
                    return true;
                }
            }
            return false;
        }, false);
    }

    @Override
    public <T extends Inventory> T query(String... names) {
        checkNotNull(names, "names");
        return this.query(inventory -> {
            String plainName = inventory.getName().get();
            for (String name : names) {
                if (plainName.equals(name)) {
                    return true;
                }
            }
            return false;
        }, false);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T extends Inventory> T query(Object... args) {
        checkNotNull(args, "args");
        return this.query(inventory -> {
            for (Object arg : args) {
                if (inventory.equals(arg)) {
                    return true;
                }
            }
            return false;
        }, false);
    }

    public abstract <T extends Inventory> T query(Predicate<Inventory> matcher, boolean nested);

    @Override
    public Optional<ItemStack> poll() {
        return this.poll(stack -> true);
    }

    public Optional<ItemStack> poll(ItemType itemType) {
        checkNotNull(itemType, "itemType");
        return this.poll(stack -> stack.getItem().equals(itemType));
    }

    public abstract Optional<ItemStack> poll(Predicate<ItemStack> matcher);

    @Override
    public Optional<ItemStack> poll(int limit) {
        return this.poll(limit, stack -> true);
    }

    public Optional<ItemStack> poll(int limit, ItemType itemType) {
        checkNotNull(itemType, "itemType");
        return this.poll(limit, stack -> stack.getItem().equals(itemType));
    }

    public abstract Optional<ItemStack> poll(int limit, Predicate<ItemStack> matcher);

    @Override
    public Optional<ItemStack> peek() {
        return this.peek(stack -> true);
    }

    public Optional<ItemStack> peek(ItemType itemType) {
        checkNotNull(itemType, "itemType");
        return this.poll(stack -> stack.getItem().equals(itemType));
    }

    public abstract Optional<ItemStack> peek(Predicate<ItemStack> matcher);

    public abstract Optional<PeekPollTransactionsResult> peekPollTransactions(Predicate<ItemStack> matcher);

    @Override
    public Optional<ItemStack> peek(int limit) {
        return this.peek(limit, stack -> true);
    }

    public Optional<ItemStack> peek(int limit, ItemType itemType) {
        checkNotNull(itemType, "itemType");
        return this.poll(limit, stack -> stack.getItem().equals(itemType));
    }

    public abstract Optional<ItemStack> peek(int limit, Predicate<ItemStack> matcher);

    public abstract Optional<PeekPollTransactionsResult> peekPollTransactions(int limit, Predicate<ItemStack> matcher);

    public abstract PeekSetTransactionsResult peekSetTransactions(@Nullable ItemStack itemStack);

    /**
     * Check whether the supplied item can be inserted into this one of the children of the
     * inventory. Returning false from this method implies that {@link #offer} <b>would
     * always return false</b> for this item.
     *
     * @param stack ItemStack to check
     * @return true if the stack is valid for one of the children of this inventory
     */
    public abstract boolean isValidItem(ItemStack stack);

    /**
     * Gets whether the specified {@link Inventory} a child is of this inventory,
     * this includes if it's a child of a child inventory.
     *
     * @param child The child inventory
     * @return Whether the inventory was a child of this inventory
     */
    public abstract boolean isChild(Inventory child);
}
