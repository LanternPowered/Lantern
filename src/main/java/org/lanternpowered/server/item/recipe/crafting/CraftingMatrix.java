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
package org.lanternpowered.server.item.recipe.crafting;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.crafting.CraftingGridInventory;

/**
 * A crafting matrix that allows {@link ItemStack}s to be reused in the crafting
 * systems, all the {@link ItemStack}s are also lazily copied.
 */
public interface CraftingMatrix {

    /**
     * Creates a {@link CraftingMatrix} for the given {@link CraftingGridInventory}.
     *
     * @param gridInventory The crafting grid inventory
     * @return The crafting matrix
     */
    static CraftingMatrix of(CraftingGridInventory gridInventory) {
        checkNotNull(gridInventory, "gridInventory");
        return new SimpleCraftingMatrix(gridInventory);
    }

    /**
     * Gets the {@link ItemStack} at the given coordinates
     * or {@link ItemStack#empty()} if none is present.
     *
     * @param x The x coordinate
     * @param y The y coordinate
     * @return The item stack
     */
    ItemStack get(int x, int y);

    /**
     * Gets the width of the crafting matrix.
     *
     * @return The width
     */
    int width();

    /**
     * Gets the height of the crafting matrix.
     *
     * @return The height
     */
    int height();

    /**
     * Creates a copy of this {@link CraftingMatrix}.
     *
     * @return The copy
     */
    CraftingMatrix copy();
}
