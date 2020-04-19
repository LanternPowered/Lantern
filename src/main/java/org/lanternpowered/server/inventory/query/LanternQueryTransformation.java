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
package org.lanternpowered.server.inventory.query;

import org.lanternpowered.server.inventory.AbstractInventory;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryTransformation;
import org.spongepowered.api.item.inventory.query.QueryOperation;

import java.util.List;

/**
 * Queries the inventory for slots in order of the query operations.
 */
public class LanternQueryTransformation implements InventoryTransformation {

    final List<QueryOperation> operations;

    LanternQueryTransformation(List<QueryOperation> operations) {
        this.operations = operations;
    }

    @Override
    public Inventory transform(Inventory inventory) {
        if (this.operations.isEmpty()) {
            return inventory;
        }
        Inventory result = ((AbstractInventory) inventory).empty();
        for (QueryOperation operation : this.operations) {
            result = result.union(inventory.query(operation));
        }
        return inventory;
    }
}
