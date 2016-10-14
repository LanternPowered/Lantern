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
package org.lanternpowered.server.data.io.store.tile;

import org.lanternpowered.server.block.tile.vanilla.LanternContainer;
import org.lanternpowered.server.data.io.store.ObjectSerializer;
import org.lanternpowered.server.data.io.store.ObjectSerializerRegistry;
import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.inventory.block.IChestInventory;
import org.lanternpowered.server.text.LanternTexts;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.property.SlotIndex;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ContainerTileEntityStore<T extends LanternContainer> extends TileEntityObjectStore<T> {

    private static final DataQuery DISPLAY_NAME = DataQuery.of("CustomName");
    private static final DataQuery ITEMS = DataQuery.of("Items");
    private static final DataQuery SLOT = DataQuery.of("Slot");

    @Override
    public void deserialize(T object, DataView dataView) {
        final List<DataView> itemViews = dataView.getViewList(ITEMS).orElse(null);
        if (itemViews != null) {
            dataView.remove(ITEMS);
            final IChestInventory inventory = (IChestInventory) object.getInventory();
            final ObjectSerializer<LanternItemStack> itemStackSerializer = ObjectSerializerRegistry.get().get(LanternItemStack.class).get();
            for (DataView itemView : itemViews) {
                final int slot = itemView.getByte(SLOT).get() & 0xff;
                final LanternItemStack itemStack = itemStackSerializer.deserialize(itemView);
                inventory.set(new SlotIndex(slot), itemStack);
            }
        }
        super.deserialize(object, dataView);
    }

    @Override
    public void serialize(T object, DataView dataView) {
        super.serialize(object, dataView);
        final ObjectSerializer<LanternItemStack> itemStackSerializer =  ObjectSerializerRegistry.get().get(LanternItemStack.class).get();
        final List<DataView> itemViews = new ArrayList<>();
        final Inventory inventory = object.getInventory();
        final Iterable<Slot> slots = inventory.slots();
        for (Slot slot : slots) {
            final Optional<ItemStack> optItemStack = slot.peek();
            if (!optItemStack.isPresent()) {
                continue;
            }
            final DataView itemView = itemStackSerializer.serialize((LanternItemStack) optItemStack.get());
            //noinspection ConstantConditions
            itemView.set(SLOT, (byte) inventory.getProperty(slot, SlotIndex.class, null).get().getValue().intValue());
            itemViews.add(itemView);
        }
        dataView.set(ITEMS, itemViews);
    }

    @Override
    public void deserializeValues(T object, SimpleValueContainer valueContainer, DataView dataView) {
        dataView.getString(DISPLAY_NAME).ifPresent(name -> valueContainer.set(Keys.DISPLAY_NAME, LanternTexts.fromLegacy(name)));
        super.deserializeValues(object, valueContainer, dataView);
    }

    @Override
    public void serializeValues(T object, SimpleValueContainer valueContainer, DataView dataView) {
        valueContainer.get(Keys.DISPLAY_NAME).ifPresent(text ->
                dataView.set(DISPLAY_NAME, LanternTexts.toLegacy(text)));
        super.serializeValues(object, valueContainer, dataView);
    }
}
