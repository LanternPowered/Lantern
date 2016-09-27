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
package org.lanternpowered.server.data.io.store.item;

import org.lanternpowered.server.data.io.store.ObjectSerializer;
import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.lanternpowered.server.data.io.store.data.DataHolderStore;
import org.lanternpowered.server.game.registry.type.item.ItemRegistryModule;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.spongepowered.api.block.BlockType;
import org.spongepowered.api.block.BlockTypes;
import org.spongepowered.api.data.DataContainer;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.data.MemoryDataContainer;
import org.spongepowered.api.data.persistence.InvalidDataException;
import org.spongepowered.api.item.ItemType;

import java.util.HashMap;
import java.util.Map;

public class ItemStackStore extends DataHolderStore<LanternItemStack> implements ObjectSerializer<LanternItemStack> {

    private static final DataQuery IDENTIFIER = DataQuery.of("id");
    private static final DataQuery QUANTITY = DataQuery.of("Count");
    private static final DataQuery DATA = DataQuery.of("Damage");
    private static final DataQuery TAG = DataQuery.of("tag");

    private final Map<ItemType, ItemTypeObjectSerializer> itemTypeSerializers = new HashMap<>();

    {
        final LogBlockItemTypeObjectSerializer logBlockItemTypeObjectSerializer = new LogBlockItemTypeObjectSerializer();
        this.add(BlockTypes.LOG, logBlockItemTypeObjectSerializer);
        this.add(BlockTypes.LOG2, logBlockItemTypeObjectSerializer);
    }

    private void add(ItemType itemType, ItemTypeObjectSerializer serializer) {
        this.itemTypeSerializers.put(itemType, serializer);
    }

    private void add(BlockType blockType, ItemTypeObjectSerializer serializer) {
        this.itemTypeSerializers.put(blockType.getItem().get(), serializer);
    }

    @Override
    public LanternItemStack deserialize(DataView dataView) throws InvalidDataException {
        String identifier = dataView.getString(IDENTIFIER).get();
        // Fix a identifier type in the mc server
        if (identifier.equals("minecraft:cooked_fished")) {
            identifier = "minecraft:cooked_fish";
        }
        final String identifier1 = identifier;
        final ItemType itemType = ItemRegistryModule.get().getById(identifier).orElseThrow(
                () -> new InvalidDataException("There is no item type with the id: " + identifier1));
        final LanternItemStack itemStack = new LanternItemStack(itemType);
        this.deserialize(itemStack, dataView);
        return itemStack;
    }

    @Override
    public DataView serialize(LanternItemStack object) {
        final DataContainer dataContainer = new MemoryDataContainer(DataView.SafetyMode.NO_DATA_CLONED);
        dataContainer.set(IDENTIFIER, object.getItem().getId());
        this.serialize(object, dataContainer);
        return dataContainer;
    }

    @Override
    public void deserialize(LanternItemStack object, DataView dataView) {
        object.setQuantity(dataView.getInt(QUANTITY).get());
        // All the extra data we will handle will be stored in the tag
        final DataView tag = dataView.getView(TAG).orElseGet(() -> new MemoryDataContainer(DataView.SafetyMode.NO_DATA_CLONED));
        tag.set(ItemTypeObjectSerializer.DATA_VALUE, dataView.getShort(DATA).get());
        super.deserialize(object, tag);
    }

    @Override
    public void serialize(LanternItemStack object, DataView dataView) {
        dataView.set(QUANTITY, (byte) object.getQuantity());
        final DataView tag = dataView.createView(TAG);
        super.serialize(object, tag);
        dataView.set(DATA, tag.getShort(ItemTypeObjectSerializer.DATA_VALUE).orElse((short) 0));
        tag.remove(ItemTypeObjectSerializer.DATA_VALUE);
        if (tag.isEmpty()) {
            dataView.remove(TAG);
        }
    }

    @Override
    public void serializeValues(LanternItemStack object, SimpleValueContainer valueContainer, DataView dataView) {
        super.serializeValues(object, valueContainer, dataView);
        final ItemTypeObjectSerializer serializer = this.itemTypeSerializers.get(object.getItem());
        if (serializer != null) {
            serializer.serializeValues(object, valueContainer, dataView);
        }
    }

    @Override
    public void deserializeValues(LanternItemStack object, SimpleValueContainer valueContainer, DataView dataView) {
        super.deserializeValues(object, valueContainer, dataView);
        final ItemTypeObjectSerializer serializer = this.itemTypeSerializers.get(object.getItem());
        if (serializer != null) {
            serializer.deserializeValues(object, valueContainer, dataView);
        }
    }
}
