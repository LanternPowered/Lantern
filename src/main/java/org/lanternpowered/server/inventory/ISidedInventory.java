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
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.util.Direction;

public interface ISidedInventory extends IInventory {

    /**
     * Gets whether this inventory can accept the specified item
     * from the specified direction.
     *
     * @param stack Stack to check
     * @param from Direction to check for insertion from
     * @return True if this inventory can accept the supplied stack from
     *         the specified direction
     */
    boolean canAccept(ItemStack stack, Direction from);

    /**
     * Attempts to insert the supplied stack into this inventory from the
     * specified direction.
     *
     * @see Inventory#offer(ItemStack)
     * @param stack Stack to insert
     * @param from Direction to check for insertion from
     * @return True if this inventory can accept the supplied stack from the
     *         specified direction
     */
    boolean offer(ItemStack stack, Direction from);

    /**
     * Gets whether automation can extract the specified item from the specified
     * direction.
     *
     * @param stack Stack to check
     * @param from Direction to check for retrieval from
     * @return True if automation can retrieve the supplied stack from the
     *         specified direction
     */
    boolean canGet(ItemStack stack, Direction from);
}
