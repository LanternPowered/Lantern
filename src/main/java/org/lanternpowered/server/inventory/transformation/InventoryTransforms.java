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
package org.lanternpowered.server.inventory.transformation;

import com.google.common.collect.Lists;
import org.lanternpowered.server.inventory.AbstractChildrenInventory;
import org.lanternpowered.server.inventory.IInventory;
import org.spongepowered.api.item.inventory.EmptyInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryTransformation;
import org.spongepowered.api.item.inventory.entity.Hotbar;
import org.spongepowered.api.item.inventory.query.QueryOperation;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;

public final class InventoryTransforms {

    private static final class QueryHolder {

        private static final QueryOperation<?> HOTBAR_OPERATION =
                QueryOperationTypes.INVENTORY_TYPE.of(Hotbar.class);
    }

    public static final InventoryTransformation NO_OP = inventory -> inventory;

    public static final InventoryTransformation EMPTY = inventory -> ((IInventory) inventory).empty();

    public static final InventoryTransformation PRIORITY_HOTBAR = inventory -> {
        final Inventory result = inventory.query(QueryHolder.HOTBAR_OPERATION);
        if (result instanceof EmptyInventory) {
            return inventory;
        }
        return result.union(inventory);
    };

    /**
     * Prioritizes the {@link Hotbar} over the target {@link Inventory}. Within the {@link Hotbar} is the slot at
     * {@link Hotbar#getSelectedSlotIndex()} prioritized over the other slots.
     */
    public static final InventoryTransformation PRIORITY_SELECTED_SLOT_AND_HOTBAR = inventory -> {
        final Inventory result = inventory.query(QueryHolder.HOTBAR_OPERATION)
                .transform(InventoryTransforms.PRIORITY_SELECTED_SLOT_AND_HOTBAR);
        if (result instanceof EmptyInventory) {
            return inventory;
        }
        return result.union(inventory);
    };

    public static final InventoryTransformation REVERSE = inventory ->
            AbstractChildrenInventory.viewBuilder()
                    .inventories(Lists.reverse(Lists.newArrayList(inventory.slots())))
                    .build();

    private InventoryTransforms() {
    }
}
