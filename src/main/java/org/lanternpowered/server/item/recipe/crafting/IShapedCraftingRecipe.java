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
