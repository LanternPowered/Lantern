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
import org.spongepowered.api.item.recipe.Recipe;
import org.spongepowered.api.item.recipe.crafting.Ingredient;

import java.util.Collection;
import java.util.Optional;

/**
 * A collection with all the {@link ItemStack}s used in a {@link Recipe} mapped
 * by it's registered {@link Ingredient}. Multiple {@link ItemStack}s may be
 * returned for one {@link Ingredient}.
 */
public interface IngredientList {

    /**
     * Gets the used {@link ItemStack} for the provided
     * {@link Ingredient}, if present.
     *
     * @param ingredient The ingredient
     * @return The item stack
     */
    Optional<ItemStack> getOne(Ingredient ingredient);

    /**
     * Gets the used {@link ItemStack}s for the provided
     * {@link Ingredient}, if present.
     *
     * @param ingredient The ingredient
     * @return The item stacks
     */
    Collection<ItemStack> getAll(Ingredient ingredient);
}
