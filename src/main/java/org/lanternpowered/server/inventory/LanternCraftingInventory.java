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

import org.lanternpowered.server.game.Lantern;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.crafting.CraftingGridInventory;
import org.spongepowered.api.item.inventory.crafting.CraftingInventory;
import org.spongepowered.api.item.inventory.crafting.CraftingOutput;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
import org.spongepowered.api.item.recipe.crafting.CraftingResult;
import org.spongepowered.api.text.translation.Translation;
import org.spongepowered.api.world.World;

import java.util.Iterator;
import java.util.Optional;

import javax.annotation.Nullable;

public class LanternCraftingInventory extends LanternGridInventory implements CraftingInventory {

    private CraftingGridInventory gridInventory;
    private CraftingOutput craftingOutput;

    public LanternCraftingInventory(@Nullable Inventory parent) {
        super(parent, null);
    }

    public LanternCraftingInventory(@Nullable Inventory parent, @Nullable Translation name) {
        super(parent, name);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void finalizeContent() {
        super.finalizeContent();

        try {
            this.gridInventory = query(CraftingGridInventory.class).first();
        } catch (ClassCastException e) {
            throw new IllegalStateException("Unable to find the CraftingGridInventory");
        }
        try {
            this.craftingOutput = query(CraftingOutput.class).first();
        } catch (ClassCastException e) {
            throw new IllegalStateException("Unable to find the CraftingOutput");
        }

        ((IInventory) this.craftingGrid).addChangeListener(slot -> {
            final Optional<CraftingResult> optResult = Lantern.getRegistry().getCraftingRecipeRegistry()
                    .getResult(this.craftingGrid, getWorld());
            this.craftingOutput.set(optResult
                    .map(CraftingResult::getResult)
                    .map(ItemStackSnapshot::createStack)
                    .orElse(null));
            System.out.println(optResult.orElse(null));
        });
    }

    @Override
    public CraftingGridInventory getCraftingGrid() {
        return this.gridInventory;
    }

    @Override
    public CraftingOutput getResult() {
        return this.craftingOutput;
    }

    @Override
    public Optional<CraftingRecipe> getRecipe(World world) {
        return this.craftingGrid.getRecipe(world);
    }

    /**
     * Attempts to get a {@link World} instance that is passed through in the
     * crafting system. By default, lets just use the first {@link World} that
     * we can find.
     *
     * @return The world
     */
    @Nullable
    protected World getWorld() {
        final Iterator<World> it = Lantern.getWorldManager().getWorlds().iterator();
        return it.hasNext() ? it.next() : null;
    }
}
