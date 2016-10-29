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

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;

import java.util.Iterator;
import java.util.Map;

public final class InventorySnapshot {

    public static final InventorySnapshot EMPTY = new InventorySnapshot(Int2ObjectMaps.emptyMap());

    /**
     * Creates a {@link InventorySnapshot} for the specified {@link Inventory}.
     *
     * @param inventory The inventory
     * @return The snapshot
     */
    public static InventorySnapshot ofInventory(Inventory inventory) {
        final Iterator<Slot> it = inventory.<Slot>slots().iterator();
        final Int2ObjectMap<ItemStackSnapshot> itemStackSnapshots = new Int2ObjectOpenHashMap<>();
        while (it.hasNext()) {
            final Slot slot = it.next();
            slot.peek().map(ItemStack::createSnapshot).ifPresent(itemStackSnapshot -> {
                //noinspection ConstantConditions
                final SlotIndex index = inventory.getProperty(slot, SlotIndex.class, null).get();
                itemStackSnapshots.put(index.getValue(), itemStackSnapshot);
            });
        }
        return new InventorySnapshot(itemStackSnapshots);
    }

    public static InventorySnapshot ofRawMap(Int2ObjectMap<ItemStackSnapshot> itemStackSnapshots) {
        return new InventorySnapshot(itemStackSnapshots);
    }

    private final Int2ObjectMap<ItemStackSnapshot> itemStackSnapshots;

    public InventorySnapshot(Map<Integer, ItemStackSnapshot> itemStackSnapshot) {
        this(new Int2ObjectOpenHashMap<>(itemStackSnapshot));
    }

    private InventorySnapshot(Int2ObjectMap<ItemStackSnapshot> itemStackSnapshots) {
        this.itemStackSnapshots = Int2ObjectMaps.unmodifiable(itemStackSnapshots);
    }

    public Int2ObjectMap<ItemStackSnapshot> getItemStackSnapshots() {
        return this.itemStackSnapshots;
    }

    public void offerTo(Inventory inventory) {
        for (Slot slot : inventory.<Slot>slots()) {
            //noinspection ConstantConditions
            final SlotIndex index = inventory.getProperty(slot, SlotIndex.class, null).get();
            final ItemStackSnapshot itemStackSnapshot = this.itemStackSnapshots.get(index.getValue());
            if (itemStackSnapshot != null) {
                slot.set(itemStackSnapshot.createStack());
            }
        }
    }
}
