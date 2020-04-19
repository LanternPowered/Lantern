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
package org.lanternpowered.server.data.io.store;

import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import org.lanternpowered.server.inventory.InventorySnapshot;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.ArrayList;
import java.util.List;

public class InventorySnapshotSerializer {

    public static final DataQuery SLOT = DataQuery.of("Slot");

    public static List<DataView> serialize(InventorySnapshot inventorySnapshot) {
        final ObjectSerializer<LanternItemStack> itemStackSerializer = ObjectSerializerRegistry.get().get(LanternItemStack.class).get();
        final List<DataView> itemViews = new ArrayList<>();
        for (Int2ObjectMap.Entry<ItemStackSnapshot> entry : inventorySnapshot.getItemStackSnapshots().int2ObjectEntrySet()) {
            final DataView itemView = itemStackSerializer.serialize((LanternItemStack) entry.getValue().createStack());
            //noinspection ConstantConditions
            itemView.set(SLOT, (byte) entry.getIntKey());
            itemViews.add(itemView);
        }
        return itemViews;
    }

    public static InventorySnapshot deserialize(List<DataView> itemDataViews) {
        final ObjectSerializer<LanternItemStack> itemStackSerializer = ObjectSerializerRegistry.get().get(LanternItemStack.class).get();
        final Int2ObjectMap<ItemStackSnapshot> itemsByIndex = new Int2ObjectOpenHashMap<>();
        for (DataView itemDataView : itemDataViews) {
            final int slot = itemDataView.getByte(SLOT).get() & 0xff;
            final ItemStackSnapshot itemStackSnapshot = itemStackSerializer.deserialize(itemDataView).createSnapshot();
            itemsByIndex.put(slot, itemStackSnapshot);
        }
        return InventorySnapshot.ofRawMap(itemsByIndex);
    }
}
