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

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import org.lanternpowered.server.item.recipe.IIngredient;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.world.World;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("ConstantConditions")
final class LanternShapelessCraftingRecipe extends LanternCraftingRecipe implements IShapelessCraftingRecipe {

    final ICraftingResultProvider resultProvider;
    private final List<Ingredient> ingredients;

    LanternShapelessCraftingRecipe(ResourceKey key, ItemStackSnapshot exemplaryResult, @Nullable String group,
            ICraftingResultProvider resultProvider, List<Ingredient> ingredients) {
        super(key, exemplaryResult, group);
        this.resultProvider = resultProvider;
        this.ingredients = ingredients;
    }

    @Override
    public List<Ingredient> getIngredientPredicates() {
        return this.ingredients;
    }

    @Override
    public Optional<Result> match(CraftingMatrix craftingMatrix, @Nullable World world, int flags) {
        final int w = craftingMatrix.width();
        final int h = craftingMatrix.height();
        final int s = w * h;

        // Check if all the ingredients can fit in the crafting grid
        if (this.ingredients.size() > s) {
            return Optional.empty();
        }

        final boolean resultItem = (flags & Flags.RESULT_ITEM) != 0;
        final boolean remainingItems = (flags & Flags.REMAINING_ITEMS) != 0;

        final List<Ingredient> ingredients = new ArrayList<>(this.ingredients);
        // Generate a ingredient map that can be useful to generate a result item
        final Multimap<Ingredient, ItemStack> ingredientItems = resultItem &&
                !(this.resultProvider instanceof ConstantCraftingResultProvider) ? HashMultimap.create() : null;
        final ImmutableList.Builder<ItemStackSnapshot> remainingItemsBuilder = remainingItems ? ImmutableList.builder() : null;

        int times = -1;
        int[][] itemQuantities = remainingItems ? new int[w][h] : null;

        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                final ItemStack itemStack = craftingMatrix.get(i, j);
                // Don't check empty item stacks
                if (itemStack.isEmpty()) {
                    remainingItemsBuilder.add(ItemStackSnapshot.empty());
                    continue;
                }
                final Iterator<Ingredient> it = ingredients.iterator();
                Optional<ItemStack> remainingItem = Optional.empty();
                boolean success = false;
                while (it.hasNext()) {
                    final IIngredient ingredient = (IIngredient) it.next();
                    if (!ingredient.test(itemStack)) {
                        continue;
                    }
                    final int quantity = ingredient.getQuantity(itemStack);
                    if (itemStack.getQuantity() < quantity) {
                        continue;
                    }
                    itemQuantities[i][j] = quantity;
                    final int times1 = itemStack.getQuantity() / quantity;
                    if (times == -1 || times1 < times) {
                        times = times1;
                    }
                    if (ingredientItems != null) {
                        ingredientItems.put(ingredient, itemStack);
                    }
                    if (remainingItemsBuilder != null) {
                        remainingItem = ingredient.getRemainingItem(itemStack);
                    }
                    it.remove();
                    success = true;
                    break;
                }
                // A faulty input ingredient was found
                if (!success) {
                    return Optional.empty();
                }
                if (remainingItemsBuilder != null) {
                    remainingItemsBuilder.add(remainingItem.map(ItemStack::createSnapshot).orElse(ItemStackSnapshot.empty()));
                }
            }
        }

        // Not all the ingredients were found
        if (!ingredients.isEmpty()) {
            return Optional.empty();
        }

        // Generate the result item
        ItemStackSnapshot resultItemStack = null;
        if (resultItem) {
            resultItemStack = this.resultProvider.getSnapshot(craftingMatrix,
                    ingredientItems == null ? null : new SimpleIngredientList(ingredientItems));
            checkNotNull(resultItemStack, "Something funky happened.");
        }

        return Optional.of(new Result(resultItemStack, remainingItemsBuilder == null ? null : remainingItemsBuilder.build(), itemQuantities, times));
    }
}
