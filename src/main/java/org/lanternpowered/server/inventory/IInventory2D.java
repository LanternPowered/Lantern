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
import org.spongepowered.api.item.inventory.Slot;
import org.spongepowered.api.item.inventory.transaction.InventoryTransactionResult;
import org.spongepowered.api.item.inventory.type.Inventory2D;
import org.spongepowered.math.vector.Vector2i;

import java.util.Optional;

public interface IInventory2D extends IInventory, Inventory2D {

    /**
     * Gets the number of columns in the inventory.
     *
     * @return The columns
     */
    int getColumns();

    /**
     * Gets the number of rows in the inventory.
     *
     * @return The rows
     */
    int getRows();

    /**
     * Returns the dimensions as a {@link Vector2i}.
     *
     * @return The dimensions
     */
    Vector2i getDimensions();

    /**
     * Gets and remove the stack at the supplied position in this Inventory.
     *
     * @see Inventory#poll()
     * @param x x coordinate
     * @param y y coordinate
     * @return ItemStack at the specified position or {@link Optional#empty()}
     *      if the slot is empty or out of bounds
     */
    Optional<ItemStack> poll(int x, int y);

    /**
     * Gets and remove the stack at the supplied position in this Inventory.
     *
     * @see Inventory#poll()
     * @param x x coordinate
     * @param y y coordinate
     * @param limit item limit
     * @return ItemStack at the specified position or {@link Optional#empty()}
     *      if the slot is empty or out of bounds
     */
    Optional<ItemStack> poll(int x, int y, int limit);

    /**
     * Gets without removing the stack at the supplied position in this
     * Inventory.
     *
     * @see Inventory#peek()
     * @param x x coordinate
     * @param y y coordinate
     * @return ItemStack at the specified position or {@link Optional#empty()}
     *      if the slot is empty or out of bounds
     */
    Optional<ItemStack> peek(int x, int y);

    /**
     * Gets without removing the stack at the supplied position in this
     * Inventory.
     *
     * @see Inventory#peek()
     * @param x x coordinate
     * @param y y coordinate
     * @param limit item limit
     * @return ItemStack at the specified position or {@link Optional#empty()}
     *      if the slot is empty or out of bounds
     */
    Optional<ItemStack> peek(int x, int y, int limit);

    /**
     * Sets the item in the specified slot.
     *
     * @see Inventory#set(ItemStack)
     * @param x x coordinate
     * @param y y coordinate
     * @param stack Item stack to insert
     * @return operation result
     */
    InventoryTransactionResult set(int x, int y, ItemStack stack);

    /**
     * Gets the {@link Slot} at the specified position.
     *
     * @param x x coordinate
     * @param y y coordinate
     * @return {@link Slot} at the specified position or
     *         {@link Optional#empty()} if the coordinates are out of bounds
     */
    Optional<Slot> getSlot(int x, int y);
}
