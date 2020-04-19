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
package org.lanternpowered.server.item.recipe.smelting;

import org.lanternpowered.server.item.recipe.IIngredient;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipe;
import org.spongepowered.api.item.recipe.smelting.SmeltingResult;
import org.spongepowered.api.text.translation.Translation;

import java.util.OptionalInt;
import java.util.function.Predicate;

public interface ISmeltingRecipe extends SmeltingRecipe {

    static Builder builder() {
        return Sponge.getRegistry().createBuilder(Builder.class);
    }

    /**
     * Gets the {@link IIngredient} of this recipe.
     *
     * @return The ingredient
     */
    IIngredient getIngredient();

    /**
     * Gets the amount of ticks that this recipe will
     * smelt for the given {@link ItemStackSnapshot}.
     *
     * @return The smelt time
     */
    default OptionalInt getSmeltTime(ItemStackSnapshot input) {
        return getSmeltTime(input.createStack());
    }

    /**
     * Gets the amount of ticks that this will
     * smelt for the given {@link ItemStack}.
     *
     * @return The smelt time
     */
    OptionalInt getSmeltTime(ItemStack input);

    interface Builder extends SmeltingRecipe.Builder {

        /**
         * Sets the {@link Ingredient} that should be used.
         *
         * @param ingredient The ingredient
         * @param exemplaryIngredient The exemplary ingredient
         * @return The result step
         */
        ResultStep ingredient(Ingredient ingredient, ItemStackSnapshot exemplaryIngredient);

        @Override
        ResultStep ingredient(Predicate<ItemStackSnapshot> ingredientPredicate, ItemStackSnapshot exemplaryIngredient);

        @Override
        ResultStep ingredient(ItemStackSnapshot ingredient);

        @Override
        ResultStep ingredient(ItemStack ingredient);

        @Override
        ResultStep ingredient(ItemType ingredient);

        @Override
        Builder from(SmeltingRecipe value);

        @Override
        Builder reset();

        interface ResultStep extends Builder, SmeltingRecipe.Builder.ResultStep {

            /**
             * Sets the {@link ISmeltingResultProvider}. When using this method,
             * {@link org.lanternpowered.server.item.recipe.smelting.ISmeltingRecipe.Builder.EndStep#experience(double)}
             * will no longer have any effect.
             *
             * @param resultProvider The smelting result provider
             * @return This builder, for chaining
             */
            Builder.EndStep result(ISmeltingResultProvider resultProvider);

            /**
             * Sets the {@link SmeltingResult}. When using this method,
             * {@link org.lanternpowered.server.item.recipe.smelting.ISmeltingRecipe.Builder.EndStep#experience(double)}
             * will no longer have any effect.
             *
             * @param result The smelting result
             * @return This builder, for chaining
             */
            Builder.EndStep result(SmeltingResult result);

            @Override
            Builder.EndStep result(ItemStackSnapshot result);

            @Override
            Builder.EndStep result(ItemStack result);

        }

        interface EndStep extends Builder, SmeltingRecipe.Builder.EndStep {

            @Override
            Builder from(SmeltingRecipe value);

            Builder.EndStep smeltTime(ISmeltingTimeProvider smeltingTimeProvider);

            Builder.EndStep smeltTime(int smeltingTime);

            @Override
            Builder.EndStep experience(double experience);

            @Override
            Builder.EndStep id(String id);

            @Override
            Builder.EndStep name(String id);

            @Override
            Builder.EndStep name(Translation name);

            @Override
            ISmeltingRecipe build();
        }
    }
}
