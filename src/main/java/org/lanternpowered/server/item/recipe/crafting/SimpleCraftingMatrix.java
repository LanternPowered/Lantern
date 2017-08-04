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

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.crafting.CraftingGridInventory;

import javax.annotation.Nullable;

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
