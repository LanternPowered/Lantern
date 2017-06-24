package org.lanternpowered.server.item.recipe.crafting;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkPositionIndex;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Multimap;
import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.item.recipe.IIngredient;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
public class LanternShapedCraftingRecipe extends LanternCraftingRecipe implements IShapedCraftingRecipe {

    final ICraftingResultProvider craftingResultProvider;
    private final Ingredient[][] ingredients;

    LanternShapedCraftingRecipe(String pluginId, String name,
            ItemStackSnapshot exemplaryResult, @Nullable String group,
            ICraftingResultProvider craftingResultProvider, Ingredient[][] ingredients) {
        super(pluginId, name, exemplaryResult, group);
        this.craftingResultProvider = craftingResultProvider;
        this.ingredients = ingredients;
    }

    @Override
    public Ingredient getIngredient(int x, int y) {
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
    Result match(CraftingMatrix craftingMatrix, boolean resultItem, boolean remainingItems) {
        final int w = craftingMatrix.width();
        final int h = craftingMatrix.height();

        int wr = getWidth();
        int hr = getHeight();

        if (wr > w || hr > h) {
            return null;
        }

        wr -= w;
        hr -= h;

        for (int i = 0; i < wr; i++) {
            for (int j = 0; j < hr; j++) {
                final Result result = matchAt(craftingMatrix, i, j, resultItem, remainingItems);
                if (result != null) {
                    return result;
                }
            }
        }

        return null;
    }

    /**
     * Matches this recipe at the given start x
     * and y coordinates within the {@link CraftingMatrix}.
     *
     * @param craftingMatrix The crafting matrix
     * @param x The initial x coordinate
     * @param y The initial y coordinate
     * @return Whether the recipe matches
     */
    private Result matchAt(CraftingMatrix craftingMatrix, int x, int y, boolean resultItem, boolean remainingItems) {
        final int cw = craftingMatrix.width();
        final int ch = craftingMatrix.height();

        final int rw = getWidth();
        final int rh = getHeight();

        final int ew = x + rw;
        final int eh = y + rh;

        // The recipe no longer fits within the grid when starting from the coordinates
        if (ew >= cw || eh >= ch) {
            return null;
        }

        // Generate a ingredient map that can be useful to generate a result item
        final Multimap<Ingredient, ItemStack> ingredientItems = resultItem &&
                !(this.craftingResultProvider instanceof ConstantCraftingResultProvider) ? HashMultimap.create() : null;

        for (int j = 0; j < rh; j++) {
            for (int i = 0; i < rw; i++) {
                final ItemStack itemStack = craftingMatrix.get(x + i, y + j);
                final Ingredient ingredient = this.ingredients[i][j];
                if (ingredient == null) {
                    if (itemStack.isEmpty()) {
                        continue;
                    }
                    return null;
                }
                if (!ingredient.test(LanternItemStack.orEmpty(itemStack))) {
                    return null;
                }
                if (ingredientItems != null) {
                    ingredientItems.put(ingredient, itemStack);
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

        // Generate a list with all the remaining items, doing this for every
        // slot, even empty ones, empty ones are added as a empty remaining
        // item.
        List<ItemStackSnapshot> remainingItemsList = null;
        if (remainingItems) {
            final ImmutableList.Builder<ItemStackSnapshot> builder = ImmutableList.builder();
            for (int j = y; j < eh; j++) {
                for (int i = x; i < ew; i++) {
                    final IIngredient ingredient = (IIngredient) this.ingredients[i][j];
                    if (ingredient == null) {
                        builder.add(ItemStackSnapshot.NONE);
                        continue;
                    }
                    final Optional<ItemStack> remainingItem = ingredient.getRemainingItem(craftingMatrix.get(i, j));
                    builder.add(remainingItem.orElse(ItemStack.empty()).createSnapshot());
                }
            }
            remainingItemsList = builder.build();
        }

        return new Result(resultItemStack, remainingItemsList);
    }
}
