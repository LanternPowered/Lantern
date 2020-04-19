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
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.item.recipe.crafting.ShapelessCraftingRecipe;
import org.spongepowered.api.text.translation.Translation;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface IShapelessCraftingRecipe extends ShapelessCraftingRecipe, ICraftingRecipe {

    /**
     * Creates a new {@link Builder}.
     *
     * @return The builder
     */
    static Builder builder() {
        return Sponge.getRegistry().createBuilder(Builder.class);
    }

    interface Builder extends ShapelessCraftingRecipe.Builder {

        @Override
        Builder from(ShapelessCraftingRecipe value);

        @Override
        Builder reset();

        @Override
        Builder.ResultStep addIngredient(Ingredient ingredient);

        /**
         * Adds the given {@link Ingredient} multiple times.
         *
         * @param ingredient The ingredient
         * @param times The amount of times the ingredient should be added
         * @return This builder, for chaining
         */
        Builder.ResultStep addIngredients(Ingredient ingredient, int times);

        interface ResultStep extends Builder, ShapelessCraftingRecipe.Builder.ResultStep {

            /**
             * Sets the {@link ICraftingResultProvider}.
             *
             * @param craftingResultProvider The crafting result provider
             * @return This builder, for chaining
             */
            Builder.EndStep result(ICraftingResultProvider craftingResultProvider);

            @Override
            Builder.EndStep result(ItemStackSnapshot result);

            @Override
            Builder.EndStep result(ItemStack result);
        }

        interface EndStep extends Builder, ShapelessCraftingRecipe.Builder.EndStep {

            @Override
            Builder from(ShapelessCraftingRecipe value);

            @Override
            Builder.EndStep group(@Nullable String name);

            @Override
            Builder.EndStep id(String id);

            @Override
            Builder.EndStep name(String id);

            @Override
            Builder.EndStep name(Translation name);

            @Override
            IShapelessCraftingRecipe build() throws IllegalStateException;
        }
    }
}
