/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.inventory;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectMaps;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperties;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.Slot;

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
        final Iterator<Slot> it = inventory.slots().iterator();
        final Int2ObjectMap<ItemStackSnapshot> itemStackSnapshots = new Int2ObjectOpenHashMap<>();
        while (it.hasNext()) {
            final ISlot slot = (ISlot) it.next();
            slot.peek().ifNotEmpty(stack -> {
                final int slotIndex = inventory.getProperty(slot, InventoryProperties.SLOT_INDEX).get().getIndex();
                itemStackSnapshots.put(slotIndex, LanternItemStackSnapshot.wrap(stack));
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
        for (Slot slot : inventory.slots()) {
            final int slotIndex = inventory.getProperty(slot, InventoryProperties.SLOT_INDEX).get().getIndex();
            final ItemStackSnapshot itemStackSnapshot = this.itemStackSnapshots.get(slotIndex);
            if (itemStackSnapshot != null) {
                slot.set(itemStackSnapshot.createStack());
            }
        }
    }
}
