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

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.item.recipe.IIngredient;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("ConstantConditions")
final class LanternShapedCraftingRecipe extends LanternCraftingRecipe implements IShapedCraftingRecipe {

    final ICraftingResultProvider resultProvider;
    private final IIngredient[][] ingredients;

    LanternShapedCraftingRecipe(CatalogKey key,
            ItemStackSnapshot exemplaryResult, @Nullable String group,
            ICraftingResultProvider resultProvider, IIngredient[][] ingredients) {
        super(key, exemplaryResult, group);
        this.resultProvider = resultProvider;
        this.ingredients = ingredients;
    }

    @Override
    public IIngredient getIngredient(int x, int y) {
        checkPositionIndex(x, getWidth());
        checkPositionIndex(y, getHeight());
        return this.ingredients[x][y];
    }

    @Override
    public int getWidth() {
        return this.ingredients.length;
    }

    @Override
    public int getHeight() {
        return this.ingredients[0].length;
    }

    @Override
    public Optional<Result> match(CraftingMatrix craftingMatrix, @Nullable World world, int flags) {
        final int w = craftingMatrix.width();
        final int h = craftingMatrix.height();

        int rw = getWidth();
        int rh = getHeight();

        if (rw > w || rh > h) {
            return Optional.empty();
        }

        rw = w - rw + 1;
        rh = h - rh + 1;

        int[][] itemQuantities = (flags & Flags.REMAINING_ITEMS) != 0 ? new int[w][h] : null;
        for (int i = 0; i < rw; i++) {
            for (int j = 0; j < rh; j++) {
                final Result result = matchAt(craftingMatrix, i, j, flags, itemQuantities);
                if (result != null) {
                    return Optional.of(result);
                }
            }
        }

        return Optional.empty();
    }

    private static void fill(int[][] matrix, int value) {
        for (int[] array : matrix) {
            Arrays.fill(array, value);
        }
    }

    /**
     * Matches this recipe at the given start x
     * and y coordinates within the {@link CraftingMatrix}.
     *
     * @param craftingMatrix The crafting matrix
     * @param startX The initial x coordinate
     * @param startY The initial y coordinate
     * @return Whether the recipe matches
     */
    @Nullable
    private Result matchAt(CraftingMatrix craftingMatrix, int startX, int startY, int flags, int[][] itemQuantities) {
        // Clear the quantities
        fill(itemQuantities, 0);

        final int cw = craftingMatrix.width();
        final int ch = craftingMatrix.height();

        final int rw = getWidth();
        final int rh = getHeight();

        final int ew = startX + rw;
        final int eh = startY + rh;

        // The recipe no longer fits within the grid when starting from the coordinates
        if (ew > cw || eh > ch) {
            return null;
        }

        final boolean resultItem = (flags & Flags.RESULT_ITEM) != 0;

        // Generate a ingredient map that can be useful to generate a result item
        final Multimap<Ingredient, ItemStack> ingredientItems = resultItem &&
                !(this.resultProvider instanceof ConstantCraftingResultProvider) ? HashMultimap.create() : null;

        int times = -1;

        for (int y = 0; y < ch; y++) {
            for (int x = 0; x < cw; x++) {
                final ItemStack itemStack = craftingMatrix.get(x, y);
                final int i = x - startX;
                final int j = y - startY;
                final IIngredient ingredient = i < 0 || i >= rw || j < 0 || j >= rh ? null : this.ingredients[i][j];
                if (ingredient == null) {
                    if (itemStack.isEmpty()) {
                        continue;
                    }
                    return null;
                }
                final int quantity = ingredient.getQuantity(itemStack);
                if (!ingredient.test(itemStack) || itemStack.getQuantity() < quantity) {
                    return null;
                }
                itemQuantities[x][y] = quantity;
                final int times1 = itemStack.getQuantity() / quantity;
                if (times == -1 || times1 < times) {
                    times = times1;
                }
                if (ingredientItems != null) {
                    ingredientItems.put(ingredient, itemStack);
                }
            }
        }

        // Generate the result item
        ItemStackSnapshot resultItemStack = null;
        if (resultItem) {
            resultItemStack = this.resultProvider.getSnapshot(craftingMatrix,
                    ingredientItems == null ? null : new SimpleIngredientList(ingredientItems));
            checkNotNull(resultItemStack, "Something funky happened.");
        }

        // Generate a list with all the remaining items, doing this for every
        // slot, even empty ones, empty ones are added as a empty remaining
        // item.
        List<ItemStackSnapshot> remainingItemsList = null;
        if ((flags & Flags.REMAINING_ITEMS) != 0) {
            final List<ItemStackSnapshot> builder = new ArrayList<>();
            for (int i = 0; i < ch * cw; i++) {
                builder.add(ItemStackSnapshot.empty());
            }
            for (int j = 0; j < rh; j++) {
                for (int i = 0; i < rw; i++) {
                    final IIngredient ingredient = this.ingredients[i][j];
                    if (ingredient != null) {
                        final Optional<ItemStack> remainingItem = ingredient.getRemainingItem(craftingMatrix.get(i, j));
                        if (remainingItem.isPresent()) {
                            builder.set((j + startY) * cw + (i + startX), LanternItemStack.toSnapshot(remainingItem.get()));
                        }
                    }
                }
            }
            remainingItemsList = ImmutableList.copyOf(builder);
        }

        return new Result(resultItemStack, remainingItemsList, itemQuantities, times);
    }
}
