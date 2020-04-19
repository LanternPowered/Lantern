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

import org.spongepowered.api.item.inventory.crafting.CraftingGridInventory;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipeRegistry;
import org.spongepowered.api.world.World;

import java.util.Optional;

public interface ICraftingRecipeRegistry extends CraftingRecipeRegistry {

    /**
     * Finds the matching recipe and creates the {@link ExtendedCraftingResult},
     * which is then returned.
     *
     * @param grid The crafting grid
     * @param world The world the player is in
     * @return The {@link ExtendedCraftingResult} if a recipe was found, or
     *         {@link Optional#empty()} if not
     */
    default Optional<ExtendedCraftingResult> getExtendedResult(CraftingGridInventory grid, World world) {
        return getExtendedResult(grid, world, 1);
    }

    /**
     * Finds the matching recipe and creates the {@link ExtendedCraftingResult},
     * which is then returned.
     *
     * @param grid The crafting grid
     * @param world The world the player is in
     * @param timesLimit The limit in amount of times that a recipe should be applied
     * @return The {@link ExtendedCraftingResult} if a recipe was found, or
     *         {@link Optional#empty()} if not
     */
    Optional<ExtendedCraftingResult> getExtendedResult(CraftingGridInventory grid, World world, int timesLimit);
}
