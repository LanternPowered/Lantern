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

import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import org.lanternpowered.server.inventory.slot.LanternSlot;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.type.OrderedInventory;
import org.spongepowered.api.text.translation.Translation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

public class LanternOrderedInventory extends AbstractChildrenInventory implements OrderedInventory {

    private static final int INVALID_INDEX = -1;

    /**
     * All the leaf {@link Slot}s of this inventory.
     */
    private final List<LanternSlot> leafSlots = new ArrayList<>();
    /**
     * All the {@link Slot}s of this inventory, may also contain indirect
     * {@link Slot}s.
     */
    protected final List<LanternSlot> slots = new ArrayList<>();
    final Object2IntMap<LanternSlot> indexBySlot = new Object2IntOpenHashMap<>();

    {
        this.indexBySlot.defaultReturnValue(INVALID_INDEX);
    }

    public LanternOrderedInventory(@Nullable Inventory parent, @Nullable Translation name) {
        super(parent, name);
    }

    /**
     * Gets the {@link Slot}s of this inventory.
     *
     * @return The slots
     */
    public List<LanternSlot> getSlots() {
        return Collections.unmodifiableList(this.slots);
    }

    /**
     * Registers a {@link Slot}.
     *
     * @param slot The slot
     * @param <T> The type of the slot
     * @return The slot for chaining
     */
    protected <T extends Slot> T registerSlot(T slot) {
        return registerSlot(slot, true);
    }

    <T extends Slot> T registerSlot(T slot, boolean leaf) {
        registerSlot(nextFreeSlotIndex(), slot, leaf);
        return slot;
    }

    @Override
    protected <T extends Inventory> T registerChild(T childInventory) {
        if (childInventory instanceof Slot) {
            registerSlot((Slot) childInventory, true);
            return childInventory;
        }
        super.registerChild(childInventory);
        if (childInventory instanceof OrderedInventory) {
            ((LanternOrderedInventory) childInventory).slots.forEach(slot -> registerSlot(slot, false));
        }
        return childInventory;
    }

    int nextFreeSlotIndex() {
        for (int i = 0; i < this.slots.size(); i++) {
            if (this.slots.get(i) == null) {
                return i;
            }
        }
        return this.slots.size();
    }

    /**
     * Registers a {@link Slot} for the specified index.
     *
     * @param index The index
     * @param slot The slot to register
     */
    void registerSlot(int index, Slot slot, boolean leaf) {
        checkNotNull(slot, "slot");
        checkArgument(this.slots.size() <= index || this.slots.get(index) == null, "The slot index %s is already in use", index);
        checkArgument(!this.indexBySlot.containsKey(slot), "The slot is already registered");
        while (this.slots.size() <= index) {
            this.slots.add(null);
        }
        this.slots.set(index, (LanternSlot) slot);
        this.indexBySlot.put((LanternSlot) slot, index);
        if (leaf) {
            this.leafSlots.add((LanternSlot) slot);
            super.registerChild(slot);
        }
    }

    @Override
    protected <T extends InventoryProperty<?, ?>> Optional<T> tryGetProperty(Inventory child, Class<T> property, @Nullable Object key) {
        if (property == SlotIndex.class && child instanceof Slot) {
            //noinspection SuspiciousMethodCalls
            final int index = this.indexBySlot.getInt(child);
            return index == INVALID_INDEX ? Optional.empty() : Optional.of(property.cast(SlotIndex.of(index)));
        }
        return super.tryGetProperty(child, property, key);
    }

    @Override
    protected <T extends InventoryProperty<?, ?>> List<T> tryGetProperties(Inventory child, Class<T> property) {
        final List<T> properties = super.tryGetProperties(child, property);
        if (property == SlotIndex.class && child instanceof Slot) {
            //noinspection SuspiciousMethodCalls
            final int index = this.indexBySlot.getInt(child);
            if (index != INVALID_INDEX) {
                properties.add(property.cast(SlotIndex.of(index)));
            }
        }
        return properties;
    }

    @Override
    protected Iterable<LanternSlot> getSlotInventories() {
        return this.leafSlots;
    }

    @Override
    public Optional<ItemStack> poll(SlotIndex index) {
        final LanternSlot slot = this.slots.get(checkNotNull(checkNotNull(index, "index").getValue(), "value"));
        return slot == null ? Optional.empty() : slot.poll();
    }

    @Override
    public Optional<ItemStack> poll(SlotIndex index, int limit) {
        final LanternSlot slot = this.slots.get(checkNotNull(checkNotNull(index, "index").getValue(), "value"));
        return slot == null ? Optional.empty() : slot.poll(limit);
    }

    @Override
    public Optional<ItemStack> peek(SlotIndex index) {
        final LanternSlot slot = this.slots.get(checkNotNull(checkNotNull(index, "index").getValue(), "value"));
        return slot == null ? Optional.empty() : slot.peek();
    }

    @Override
    public Optional<ItemStack> peek(SlotIndex index, int limit) {
        final LanternSlot slot = this.slots.get(checkNotNull(checkNotNull(index, "index").getValue(), "value"));
        return slot == null ? Optional.empty() : slot.peek(limit);
    }

    @Override
    public InventoryTransactionResult set(SlotIndex index, ItemStack stack) {
        final LanternSlot slot = this.slots.get(checkNotNull(checkNotNull(index, "index").getValue(), "value"));
        return slot == null ? InventoryTransactionResult.failNoTransactions() : slot.set(stack);
    }

    @Override
    public Optional<Slot> getSlot(SlotIndex index) {
        return Optional.ofNullable(this.slots.get(checkNotNull(checkNotNull(index, "index").getValue(), "value")));
    }

    /**
     * Gets the {@link Slot} for the specified index.
     *
     * @param index The slot index
     * @return The slot if found
     */
    public Optional<LanternSlot> getSlotAt(int index) {
        return index < 0 || index >= this.slots.size() ? Optional.empty() : Optional.ofNullable(this.slots.get(index));
    }

    /**
     * Gets the index of the {@link Slot} in this ordered inventory,
     * may return {@code -1} if the slot was not found.
     *
     * @param slot The slot
     * @return The slot index
     */
    public int getSlotIndex(Slot slot) {
        return this.indexBySlot.getInt(slot);
    }
}
