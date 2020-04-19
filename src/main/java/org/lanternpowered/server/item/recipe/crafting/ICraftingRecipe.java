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

import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.inventory.crafting.CraftingGridInventory;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
import org.spongepowered.api.item.recipe.crafting.CraftingResult;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

/**
 * A extension of {@link CraftingRecipe} that avoids making multiple
 * copies of {@link ItemStack}s that can be reused. All the {@link ItemStack}s
 * in the {@link CraftingMatrix} may NOT BE MODIFIED, otherwise next operations
 * may end up in trouble, always make a copy of these stacks if you want
 * to use them.
 */
public interface ICraftingRecipe extends CraftingRecipe {

    @Override
    default Optional<String> getGroup() {
        return Optional.empty();
    }

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
    default Optional<ExtendedCraftingResult> getExtendedResult(CraftingMatrix craftingMatrix, @Nullable World world) {
        return getExtendedResult(craftingMatrix, world, 1);
    }

    /**
     * Attempts to get a {@link CraftingResult} for the given
     * {@link CraftingMatrix} and {@link World}.
     *
     * @param craftingMatrix The crafting matrix
     * @param world The world
     * @return The crafting result if successful, otherwise {@link Optional#empty()}
     */
    Optional<ExtendedCraftingResult> getExtendedResult(CraftingMatrix craftingMatrix, @Nullable World world, int timesLimit);

    /**
     * Attempts to get a {@link CraftingResult} for the given
     * {@link CraftingMatrix} and {@link World}.
     *
     * @param craftingMatrix The crafting matrix
     * @param world The world
     * @return The crafting result if successful, otherwise {@link Optional#empty()}
     */
    default Optional<CraftingResult> getResult(CraftingMatrix craftingMatrix, World world) {
        return getExtendedResult(craftingMatrix, world).map(ExtendedCraftingResult::getResult);
    }

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

    /**
     * Creates a new {@link IShapedCraftingRecipe.Builder}.
     *
     * @return The builder
     */
    static IShapedCraftingRecipe.Builder shapedBuilder() {
        return Sponge.getRegistry().createBuilder(IShapedCraftingRecipe.Builder.class);
    }

    /**
     * Creates a new {@link IShapelessCraftingRecipe.Builder}.
     *
     * @return The builder
     */
    static IShapelessCraftingRecipe.Builder shapelessBuilder() {
        return Sponge.getRegistry().createBuilder(IShapelessCraftingRecipe.Builder.class);
    }
}
