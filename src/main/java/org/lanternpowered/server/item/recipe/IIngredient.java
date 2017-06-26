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
package org.lanternpowered.server.item.recipe;

import org.spongepowered.api.GameDictionary;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An extension of {@link Ingredient} that allows a
 * remaining item to be provided.
 */
public interface IIngredient extends Ingredient {

    /**
     * Creates a new {@link Builder} to build an {@link IIngredient}.
     *
     * @return The new builder
     */
    static Builder builder() {
        return Sponge.getRegistry().createBuilder(Builder.class);
    }

    /**
     * Gets the remaining {@link ItemStack}, if present.
     *
     * @return The remaining item
     */
    default Optional<ItemStack> getRemainingItem(ItemStack itemStack) {
        return Optional.empty();
    }

    /**
     * Gets the remaining {@link ItemStack}, if present.
     *
     * @return The remaining item
     */
    default Optional<ItemStack> getRemainingItem(ItemStackSnapshot itemStack) {
        return getRemainingItem(itemStack.createStack());
    }

    /**
     * Tests whether the given {@link ItemStackSnapshot} is valid.
     *
     * @param itemStackSnapshot The item stack snapshot
     * @return Whether the item stack snapshot is valid
     */
    default boolean test(ItemStackSnapshot itemStackSnapshot) {
        return test(itemStackSnapshot.createStack());
    }

    /**
     * Gets the quantity of input items that are required to
     * smelt, for the given {@link ItemStackSnapshot}.
     *
     * @param itemStackSnapshot The item stack snapshot
     * @return The quantity
     */
    int getQuantity(ItemStackSnapshot itemStackSnapshot);

    /**
     * A builder to construct {@link IIngredient}s.
     */
    interface Builder extends Ingredient.Builder {

        /**
         * Applies the quantity of input items that are required
         * to smelt the item.
         *
         * @param quantity The quantity
         * @return This builder, for chaining
         */
        Builder withQuantity(int quantity);

        /**
         * Applies the {@link ItemType} that should be returned when
         * a recipe is used.
         *
         * @param type The item type
         * @return This builder, for chaining
         */
        Builder withRemaining(ItemType type);

        /**
         * Applies the {@link ItemStack} that should be returned when
         * a recipe is used.
         *
         * @param item The item stack
         * @return This builder, for chaining
         */
        Builder withRemaining(ItemStack item);

        /**
         * Applies the {@link ItemStackSnapshot} that should be returned when
         * a recipe is used.
         *
         * @param item The item stack snapshot
         * @return This builder, for chaining
         */
        Builder withRemaining(ItemStackSnapshot item);

        /**
         * Applies the {@link Function} that will generate the returned
         * {@link ItemStack} based on the input {@link ItemStack}.
         *
         * @param provider The provider
         * @return This builder, for chaining
         */
        Builder withRemaining(Function<ItemStack, ItemStack> provider);

        @Override
        Builder with(Predicate<ItemStack> predicate);

        @Override
        Builder with(GameDictionary.Entry entry);

        @Override
        Builder with(ItemType... types);

        @Override
        Builder with(ItemStack... items);

        @Override
        Builder with(ItemStackSnapshot... items);

        @Override
        Builder withDisplay(ItemType... types);

        @Override
        Builder withDisplay(ItemStack... items);

        @Override
        Builder withDisplay(ItemStackSnapshot... items);

        @Override
        IIngredient build();
    }
}
