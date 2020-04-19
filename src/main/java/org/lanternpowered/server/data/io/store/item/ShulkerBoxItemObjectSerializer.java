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
package org.lanternpowered.server.data.io.store.item;

import org.lanternpowered.server.data.io.store.InventorySnapshotSerializer;
import org.lanternpowered.server.data.io.store.SimpleValueContainer;
import org.lanternpowered.server.data.key.LanternKeys;
import org.spongepowered.api.data.persistence.DataQuery;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.List;

public class ShulkerBoxItemObjectSerializer extends ItemTypeObjectSerializer {

    private static final DataQuery ITEMS = DataQuery.of('.', "BlockEntityTag.Items");

    @Override
    public void serializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
        super.serializeValues(itemStack, valueContainer, dataView);
        final List<DataView> itemDataViews = InventorySnapshotSerializer.serialize(valueContainer.remove(LanternKeys.INVENTORY_SNAPSHOT).get());
        if (!itemDataViews.isEmpty()) {
            dataView.set(ITEMS, itemDataViews);
        }
    }

    @Override
    public void deserializeValues(ItemStack itemStack, SimpleValueContainer valueContainer, DataView dataView) {
        super.deserializeValues(itemStack, valueContainer, dataView);
        dataView.getViewList(ITEMS).ifPresent(itemDataViews ->
                valueContainer.set(LanternKeys.INVENTORY_SNAPSHOT, InventorySnapshotSerializer.deserialize(itemDataViews)));
    }
}
