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

import org.lanternpowered.server.item.recipe.LanternRecipeRegistry;
import org.lanternpowered.server.item.recipe.LanternRecipeRegistryModule;
import org.spongepowered.api.item.inventory.crafting.CraftingGridInventory;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipeRegistry;
import org.spongepowered.api.item.recipe.crafting.CraftingResult;
import org.spongepowered.api.world.World;

import java.util.Optional;

public class LanternCraftingRecipeRegistry extends LanternRecipeRegistry<CraftingRecipe, CraftingRecipe>
        implements CraftingRecipeRegistry {

    public LanternCraftingRecipeRegistry(LanternRecipeRegistryModule<CraftingRecipe> registryModule) {
        super(registryModule);
    }

    @Override
    public Optional<CraftingRecipe> findMatchingRecipe(CraftingGridInventory grid, World world) {
        final CraftingMatrix craftingMatrix = CraftingMatrix.of(grid);
        for (CraftingRecipe recipe : getRecipes()) {
            final boolean result;
            if (recipe instanceof ICraftingRecipe) {
                result = ((ICraftingRecipe) recipe).isValid(craftingMatrix, world);
            } else {
                result = recipe.isValid(grid, world);
            }
            if (result) {
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<CraftingResult> getResult(CraftingGridInventory grid, World world) {
        final CraftingMatrix craftingMatrix = CraftingMatrix.of(grid);
        for (CraftingRecipe recipe : getRecipes()) {
            final Optional<CraftingResult> result;
            if (recipe instanceof ICraftingRecipe) {
                result = ((ICraftingRecipe) recipe).getResult(craftingMatrix, world);
            } else {
                result = recipe.getResult(grid, world);
            }
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }
}
