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
package org.lanternpowered.server.inventory;

import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryProperty;
import org.spongepowered.api.item.inventory.ItemStack;

import java.util.Optional;
import java.util.function.Predicate;

public interface IInventory extends Inventory {

    @Override
    IInventory parent();

    /**
     * Adds a {@link ContainerViewListener} to this {@link Inventory}.
     *
     * @param listener The listener
     */
    void add(ContainerViewListener listener);

    Optional<ItemStack> poll(ItemType itemType);

    Optional<ItemStack> poll(Predicate<ItemStack> matcher);

    Optional<ItemStack> poll(int limit, ItemType itemType);

    Optional<ItemStack> poll(int limit, Predicate<ItemStack> matcher);

    Optional<ItemStack> peek(ItemType itemType);

    Optional<ItemStack> peek(Predicate<ItemStack> matcher);

    Optional<ItemStack> peek(int limit, ItemType itemType);

    Optional<ItemStack> peek(int limit, Predicate<ItemStack> matcher);

    <T extends Inventory> T query(Predicate<Inventory> matcher, boolean nested);

    /**
     * Check whether the supplied item can be inserted into this one of the children of the
     * inventory. Returning false from this method implies that {@link #offer} <b>would
     * always return false</b> for this item.
     *
     * @param stack ItemStack to check
     * @return true if the stack is valid for one of the children of this inventory
     */
    boolean isValidItem(ItemStack stack);

    /**
     * Gets whether the specified {@link Inventory} a child is of this inventory,
     * this includes if it's a child of a child inventory.
     *
     * @param child The child inventory
     * @return Whether the inventory was a child of this inventory
     */
    boolean isChild(Inventory child);

    /**
     * Gets the amount of slots that are present in this inventory.
     *
     * @return The slot count
     */
    int slotCount();

    /**
     * Gets whether this {@link Inventory} a property
     * contains of the specified type.
     *
     * @param property The property type
     * @return Whether a property exists
     */
    boolean hasProperty(Class<? extends InventoryProperty<?, ?>> property);

    boolean hasProperty(InventoryProperty<?, ?> property);

    boolean hasProperty(Inventory child, InventoryProperty<?, ?> property);

}
