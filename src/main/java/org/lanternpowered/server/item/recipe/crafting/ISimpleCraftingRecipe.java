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

import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.CraftingResult;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

/**
 * A simplified {@link ICraftingRecipe} that allows the crafting behavior
 * to be implemented in one method and providing an implementation of all the
 * other methods.
 */
public interface ISimpleCraftingRecipe extends ICraftingRecipe {

    @Override
    default boolean isValid(CraftingMatrix craftingMatrix, World world) {
        return match(craftingMatrix, null, 0).isPresent();
    }

    @Override
    default ItemStackSnapshot getResult(CraftingMatrix craftingMatrix) {
        return match(craftingMatrix, null, Flags.RESULT_ITEM)
                .map(result -> result.getResultItem().get())
                .orElseThrow(() -> new IllegalStateException("test is false"));
    }

    @Override
    default List<ItemStackSnapshot> getRemainingItems(CraftingMatrix craftingMatrix) {
        return match(craftingMatrix, null, Flags.REMAINING_ITEMS)
                .map(result -> result.getRemainingItems().get())
                .orElseThrow(() -> new IllegalStateException("test is false"));
    }

    @Override
    default Optional<ExtendedCraftingResult> getExtendedResult(CraftingMatrix craftingMatrix, @Nullable World world, int timesLimit) {
        return match(craftingMatrix, world, Flags.RESULT_ITEM | Flags.REMAINING_ITEMS)
                .map(result -> new ExtendedCraftingResult(new CraftingResult(
                        result.getResultItem().get(),
                        result.getRemainingItems().get()),
                        craftingMatrix,
                        result.getMaxTimes(),
                        result.getItemQuantities()));
    }

    /**
     * Attempts to match the specified {@link CraftingMatrix}. The {@code flags} will
     * specify what should be included in the result object {@see Flags}.
     *
     * @param craftingMatrix The crafting matrix
     * @param flags The flags
     * @return The result, if present
     */
    Optional<Result> match(CraftingMatrix craftingMatrix, @Nullable World world, int flags);

    /**
     * The result flags.
     */
    final class Flags {

        /**
         * The result item should be included in a {@link Result}.
         */
        public static final int RESULT_ITEM = 0x1;

        /**
         * The remaining items should be included in a {@link Result}.
         */
        public static final int REMAINING_ITEMS = 0x2;

        private Flags() {}
    }

    /**
     * A multi purpose result that simplifies the
     * implementation of crafting recipes.
     */
    final class Result {

        @Nullable private final ItemStackSnapshot resultItem;
        @Nullable private final List<ItemStackSnapshot> remainingItems;
        @Nullable private final int[][] itemQuantities;
        private final int maxTimes;

        /**
         * Constructs a new {@link Result}.
         * @param resultItem The result item, not affected by the times
         * @param remainingItems The remaining items
         * @param maxTimes The maximum amout of times the recipe can be applied
         */
        public Result(
                @Nullable ItemStackSnapshot resultItem,
                @Nullable List<ItemStackSnapshot> remainingItems,
                int maxTimes) {
            this(resultItem, remainingItems, null, maxTimes);
        }

        Result(
                @Nullable ItemStackSnapshot resultItem,
                @Nullable List<ItemStackSnapshot> remainingItems,
                @Nullable int[][] itemQuantities,
                int maxTimes) {
            this.itemQuantities = itemQuantities;
            this.remainingItems = remainingItems;
            this.resultItem = resultItem;
            this.maxTimes = maxTimes;
        }

        /**
         * Gets the result {@link ItemStackSnapshot}, if present.
         *
         * @return The result item
         */
        public Optional<ItemStackSnapshot> getResultItem() {
            return Optional.ofNullable(this.resultItem);
        }

        /**
         * Gets the remaining {@link ItemStackSnapshot}s.
         *
         * @return The remaining items
         */
        public Optional<List<ItemStackSnapshot>> getRemainingItems() {
            return Optional.ofNullable(this.remainingItems);
        }

        public int getMaxTimes() {
            return this.maxTimes;
        }

        @Nullable
        int[][] getItemQuantities() {
            return this.itemQuantities;
        }
    }
}
