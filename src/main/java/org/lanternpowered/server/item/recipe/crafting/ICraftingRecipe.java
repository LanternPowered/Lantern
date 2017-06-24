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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.crafting.CraftingGridInventory;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
import org.spongepowered.api.item.recipe.crafting.CraftingResult;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

/**
 * A extension of {@link CraftingRecipe} that avoids making multiple
 * copies of {@link ItemStack}s that can be reused. All the {@link ItemStack}s
 * in the grid arrays may NOT BE MODIFIED, otherwise next operations
 * may end up in trouble, always make a copy of these stacks if you want
 * to use them.
 */
public interface ICraftingRecipe extends CraftingRecipe {

    /**
     * Checks if the given {@link CraftingMatrix} fits the required constraints
     * to craft this {@link ICraftingRecipe}.
     *
     * @param craftingMatrix The crafting matrix to check for validity
     * @param world The world this recipe would be used in
     * @return True if the given input matches this recipe's requirements
     */
    boolean isValid(CraftingMatrix craftingMatrix, World world);

    /**
     * Gets the result for the given {@link ItemStack}s in the {@link CraftingMatrix}.
     *
     * @param craftingMatrix The crafting matrix
     * @return The result
     */
    ItemStackSnapshot getResult(CraftingMatrix craftingMatrix);

    /**
     * Gets the remaining items for the given {@link ItemStack}s in the {@link CraftingMatrix}.
     *
     * @param craftingMatrix The crafting matrix
     * @return The result
     */
    List<ItemStackSnapshot> getRemainingItems(CraftingMatrix craftingMatrix);

    /**
     * Attempts to get a {@link CraftingResult} for the given
     * {@link CraftingMatrix} and {@link World}.
     *
     * @param craftingMatrix The crafting matrix
     * @param world The world
     * @return The crafting result if successful, otherwise {@link Optional#empty()}
     */
    Optional<CraftingResult> getResult(CraftingMatrix craftingMatrix, World world);

    @Override
    default boolean isValid(CraftingGridInventory grid, World world) {
        return isValid(CraftingMatrix.of(grid), world);
    }

    @Override
    default ItemStackSnapshot getResult(CraftingGridInventory grid) {
        return getResult(CraftingMatrix.of(grid));
    }

    @Override
    default List<ItemStackSnapshot> getRemainingItems(CraftingGridInventory grid) {
        return getRemainingItems(CraftingMatrix.of(grid));
    }

    @Override
    default Optional<CraftingResult> getResult(CraftingGridInventory grid, World world) {
        return getResult(CraftingMatrix.of(grid), world);
    }

    static IShapedCraftingRecipe.Builder shapedBuilder() {
        return Sponge.getRegistry().createBuilder(IShapedCraftingRecipe.Builder.class);
    }

    static IShapelessCraftingRecipe.Builder shapelessBuilder() {
        return Sponge.getRegistry().createBuilder(IShapelessCraftingRecipe.Builder.class);
    }
}
