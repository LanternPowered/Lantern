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

import org.lanternpowered.server.item.recipe.IIngredient;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.item.recipe.crafting.ShapedCraftingRecipe;
import org.spongepowered.api.text.translation.Translation;

import java.util.Map;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface IShapedCraftingRecipe extends ICraftingRecipe, ShapedCraftingRecipe {

    /**
     * Creates a new {@link Builder}.
     *
     * @return The builder
     */
    static Builder builder() {
        return Sponge.getRegistry().createBuilder(Builder.class);
    }

    @Override
    IIngredient getIngredient(int x, int y);

    interface Builder extends ShapedCraftingRecipe.Builder {

        @Override
        AisleStep aisle(String... aisle);

        @Override
        Builder from(ShapedCraftingRecipe value);

        @Override
        Builder reset();

        interface AisleStep extends Builder, ShapedCraftingRecipe.Builder.AisleStep {

            interface ResultStep extends Builder.AisleStep, Builder.ResultStep, ShapedCraftingRecipe.Builder.AisleStep.ResultStep {}

            @Override
            ResultStep where(char symbol, @Nullable Ingredient ingredient) throws IllegalArgumentException;

            @Override
            ResultStep where(Map<Character, Ingredient> ingredientMap) throws IllegalArgumentException;
        }

        @Override
        RowsStep rows();

        interface RowsStep extends Builder, ShapedCraftingRecipe.Builder.RowsStep {

            interface ResultStep extends Builder.RowsStep, Builder.ResultStep, ShapedCraftingRecipe.Builder.RowsStep.ResultStep {}

            @Override
            ResultStep row(Ingredient... ingredients);

            @Override
            ResultStep row(int skip, Ingredient... ingredients);
        }

        interface ResultStep extends Builder, ShapedCraftingRecipe.Builder.ResultStep {

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

        interface EndStep extends Builder, ShapedCraftingRecipe.Builder.EndStep {

            @Override
            Builder from(ShapedCraftingRecipe value);

            @Override
            Builder.EndStep group(@Nullable String name);

            @Override
            Builder.EndStep id(String id);

            @Override
            Builder.EndStep name(String id);

            @Override
            Builder.EndStep name(Translation name);

            @Override
            IShapedCraftingRecipe build() throws IllegalStateException;
        }
    }
}
