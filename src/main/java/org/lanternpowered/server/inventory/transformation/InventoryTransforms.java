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
package org.lanternpowered.server.inventory.transformation;

import com.google.common.collect.Lists;
import org.lanternpowered.server.inventory.AbstractOrderedInventory;
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

    public static final InventoryTransformation REVERSE = inventory ->
            AbstractOrderedInventory.viewBuilder()
                    .inventories(Lists.reverse(Lists.newArrayList(inventory.slots())))
                    .build();

    private InventoryTransforms() {
    }
}
