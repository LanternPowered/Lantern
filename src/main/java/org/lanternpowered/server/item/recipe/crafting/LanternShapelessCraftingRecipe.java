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

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import org.lanternpowered.server.item.recipe.IIngredient;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
final class LanternShapelessCraftingRecipe extends LanternCraftingRecipe implements IShapelessCraftingRecipe {

    final ICraftingResultProvider resultProvider;
    private final List<Ingredient> ingredients;

    LanternShapelessCraftingRecipe(String pluginId, String name, ItemStackSnapshot exemplaryResult, @Nullable String group,
            ICraftingResultProvider resultProvider, List<Ingredient> ingredients) {
        super(pluginId, name, exemplaryResult, group);
        this.resultProvider = resultProvider;
        this.ingredients = ingredients;
    }

    @Override
    public List<Ingredient> getIngredientPredicates() {
        return this.ingredients;
    }

    @Override
    Result match(CraftingMatrix craftingMatrix, boolean resultItem, boolean remainingItems) {
        final int w = craftingMatrix.width();
        final int h = craftingMatrix.height();
        final int s = w * h;

        // Check if all the ingredients can fit in the crafting grid
        if (this.ingredients.size() > s) {
            return null;
        }

        final List<Ingredient> ingredients = new ArrayList<>(this.ingredients);
        // Generate a ingredient map that can be useful to generate a result item
        final Multimap<Ingredient, ItemStack> ingredientItems = resultItem &&
                !(this.resultProvider instanceof ConstantCraftingResultProvider) ? HashMultimap.create() : null;
        final ImmutableList.Builder<ItemStackSnapshot> remainingItemsBuilder = remainingItems ? ImmutableList.builder() : null;

        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                final ItemStack itemStack = craftingMatrix.get(i, j);
                // Don't check empty item stacks
                if (itemStack.isEmpty()) {
                    remainingItemsBuilder.add(ItemStackSnapshot.NONE);
                    continue;
                }
                final Iterator<Ingredient> it = ingredients.iterator();
                Optional<ItemStack> remainingItem = Optional.empty();
                boolean success = false;
                while (it.hasNext()) {
                    final Ingredient ingredient = it.next();
                    if (ingredient.test(itemStack)) {
                        if (ingredientItems != null) {
                            ingredientItems.put(ingredient, itemStack);
                        }
                        if (remainingItemsBuilder != null) {
                            remainingItem = ((IIngredient) ingredient).getRemainingItem(itemStack);
                        }
                        it.remove();
                        success = true;
                        break;
                    }
                }
                // A faulty input ingredient was found
                if (!success) {
                    return null;
                }
                if (remainingItemsBuilder != null) {
                    remainingItemsBuilder.add(remainingItem.map(ItemStack::createSnapshot).orElse(ItemStackSnapshot.NONE));
                }
            }
        }

        // Not all the ingredients were found
        if (!ingredients.isEmpty()) {
            return null;
        }

        // Generate the result item
        ItemStack resultItemStack = null;
        if (resultItem) {
            resultItemStack = this.resultProvider.get(craftingMatrix,
                    ingredientItems == null ? null : new SimpleIngredientList(ingredientItems));
            checkNotNull(resultItemStack, "Something funky happened.");
        }

        return new Result(resultItemStack, remainingItemsBuilder == null ? null : remainingItemsBuilder.build());
    }
}
