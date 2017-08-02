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
