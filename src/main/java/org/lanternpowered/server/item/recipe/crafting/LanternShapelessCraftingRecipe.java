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

    final ICraftingResultProvider craftingResultProvider;
    final List<Ingredient> ingredients;

    LanternShapelessCraftingRecipe(String pluginId, String name, ItemStackSnapshot exemplaryResult, @Nullable String group,
            ICraftingResultProvider craftingResultProvider, List<Ingredient> ingredients) {
        super(pluginId, name, exemplaryResult, group);
        this.craftingResultProvider = craftingResultProvider;
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
                !(this.craftingResultProvider instanceof ConstantCraftingResultProvider) ? HashMultimap.create() : null;
        final ImmutableList.Builder<ItemStackSnapshot> remainingItemsBuilder = remainingItems ? ImmutableList.builder() : null;

        for (int j = 0; j < h; j++) {
            for (int i = 0; i < w; i++) {
                final ItemStack itemStack = craftingMatrix.get(i, j);
                // Don't check empty item stacks
                if (itemStack.isEmpty()) {
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

        // Generate the result item
        ItemStack resultItemStack = null;
        if (resultItem) {
            resultItemStack = this.craftingResultProvider.get(craftingMatrix,
                    ingredientItems == null ? null : new IngredientList(ingredientItems));
            checkNotNull(resultItemStack, "Something funky happened.");
        }

        return new Result(resultItemStack, remainingItemsBuilder == null ? null : remainingItemsBuilder.build());
    }
}
