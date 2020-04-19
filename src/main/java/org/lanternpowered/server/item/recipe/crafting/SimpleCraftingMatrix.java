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

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.crafting.CraftingGridInventory;

import org.checkerframework.checker.nullness.qual.Nullable;

final class SimpleCraftingMatrix implements ICraftingMatrix {

    @Nullable private ItemStack[][] matrix;
    private final CraftingGridInventory grid;

    SimpleCraftingMatrix(CraftingGridInventory grid) {
        this.grid = grid;
    }

    @Override
    public void set(int x, int y, ItemStack itemStack) {
        if (this.matrix == null) {
            this.matrix = new ItemStack[this.grid.getColumns()][this.grid.getRows()];
        }
        this.matrix[x][y] = itemStack;
    }

    @Override
    public ItemStack get(int x, int y) {
        if (this.matrix == null) {
            this.matrix = new ItemStack[this.grid.getColumns()][this.grid.getRows()];
        }
        ItemStack itemStack = this.matrix[x][y];
        if (itemStack == null) {
            itemStack = this.matrix[x][y] = this.grid.peek(x, y).orElse(ItemStack.empty());
        }
        return itemStack;
    }

    @Override
    public int width() {
        return this.grid.getColumns();
    }

    @Override
    public int height() {
        return this.grid.getRows();
    }

    @Override
    public CraftingMatrix copy() {
        return new SimpleCraftingMatrix(this.grid);
    }
}
