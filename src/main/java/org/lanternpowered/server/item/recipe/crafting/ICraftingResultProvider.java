package org.lanternpowered.server.item.recipe.crafting;

import static com.google.common.base.Preconditions.checkNotNull;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

@FunctionalInterface
public interface ICraftingResultProvider {

    /**
     * Creates a constant {@link ICraftingResultProvider} for
     * the given {@link ItemStackSnapshot}.
     *
     * @param itemStackSnapshot The item stack snapshot
     * @return The crafting result provider
     */
    static ICraftingResultProvider constant(ItemStackSnapshot itemStackSnapshot) {
        checkNotNull(itemStackSnapshot, "itemStackSnapshot");
        return new ConstantCraftingResultProvider(itemStackSnapshot);
    }

    /**
     * Gets the result {@link ItemStack} for the given {@link CraftingMatrix}. The method
     * should always provide a result, even though the {@link IngredientList} is empty,
     * this is the case when generating a exemplary result.
     *
     * @param craftingMatrix The crafting matrix
     * @param ingredientList The ingredient list supplier,
     *                       based on the {@link CraftingMatrix} and the used recipe.
     * @return The item stack
     */
    ItemStack get(CraftingMatrix craftingMatrix, IngredientList ingredientList);

    /**
     * Gets the result {@link ItemStackSnapshot} for the given {@link CraftingMatrix}.
     * The method should always provide a result, even though the {@link IngredientList}
     * is empty, this is the case when generating a exemplary result.
     *
     * @param craftingMatrix The crafting matrix
     * @param ingredientList The ingredient list supplier,
     *                       based on the {@link CraftingMatrix} and the used recipe.
     * @return The item stack snapshot
     */
    default ItemStackSnapshot getSnapshot(CraftingMatrix craftingMatrix, IngredientList ingredientList) {
        return get(craftingMatrix, ingredientList).createSnapshot();
    }
}
