package org.lanternpowered.server.item.recipe.crafting;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

final class ConstantCraftingResultProvider implements ICraftingResultProvider {

    private final ItemStackSnapshot snapshot;

    ConstantCraftingResultProvider(ItemStackSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    @Override
    public ItemStack get(CraftingMatrix craftingMatrix, IngredientList ingredientList) {
        return getSnapshot(craftingMatrix, ingredientList).createStack();
    }

    @Override
    public ItemStackSnapshot getSnapshot(CraftingMatrix craftingMatrix, IngredientList ingredientList) {
        return this.snapshot;
    }
}
