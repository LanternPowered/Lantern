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

import org.spongepowered.api.item.inventory.Inventory;

import java.util.List;

/**
 * Represents a inventory that is the
 * result of a query operation.
 */
public interface IQueryInventory extends IInventory {

    /**
     * Gets the first child of this inventory.
     *
     * @return The first child
     * @throws IllegalStateException If there are no children
     */
    default IInventory first() {
        final List<Inventory> children = children();
        if (children.isEmpty()) {
            throw new IllegalStateException("No children");
        }
        return (IInventory) children.get(0);
    }
}
