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

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

final class ConstantCraftingResultProvider implements ICraftingResultProvider {

    private final ItemStackSnapshot snapshot;

    ConstantCraftingResultProvider(ItemStackSnapshot snapshot) {
        this.snapshot = snapshot;
    }

    @Override
    public ItemStack get(CraftingMatrix craftingMatrix, IngredientList ingredientList) {
        return this.snapshot.createStack();
    }

    @Override
    public ItemStackSnapshot getSnapshot(CraftingMatrix craftingMatrix, IngredientList ingredientList) {
        return this.snapshot;
    }
}
