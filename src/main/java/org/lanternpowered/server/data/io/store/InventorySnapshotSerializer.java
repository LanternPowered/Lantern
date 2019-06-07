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
