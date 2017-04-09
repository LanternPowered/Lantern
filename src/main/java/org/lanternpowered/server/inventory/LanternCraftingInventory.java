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

import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.crafting.CraftingInventory;
import org.spongepowered.api.item.inventory.crafting.CraftingOutput;
import org.spongepowered.api.item.inventory.type.GridInventory;
import org.spongepowered.api.item.recipe.Recipe;
import org.spongepowered.api.text.translation.Translation;

import java.util.Optional;

import javax.annotation.Nullable;

public class LanternCraftingInventory extends LanternGridInventory implements CraftingInventory {

    private GridInventory gridInventory;
    private CraftingOutput craftingOutput;

    public LanternCraftingInventory(@Nullable Inventory parent) {
        super(parent, null);
    }

    public LanternCraftingInventory(@Nullable Inventory parent, @Nullable Translation name) {
        super(parent, name);
    }

    @Override
    protected void finalizeContent() {
        super.finalizeContent();

        try {
            this.gridInventory = query(GridInventory.class).first();
        } catch (ClassCastException e) {
            throw new IllegalStateException("Unable to find the GridInventory");
        }
        try {
            this.craftingOutput = query(CraftingOutput.class).first();
        } catch (ClassCastException e) {
            throw new IllegalStateException("Unable to find the CraftingOutput");
        }
    }

    @Override
    public GridInventory getCraftingGrid() {
        return this.gridInventory;
    }

    @Override
    public CraftingOutput getResult() {
        return this.craftingOutput;
    }

    @Override
    public Optional<Recipe> getRecipe() {
        return Optional.empty();
    }
}
