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
package org.lanternpowered.server.item.recipe.smelting;

import org.lanternpowered.server.item.recipe.IIngredient;
import org.lanternpowered.server.item.recipe.LanternRecipe;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.smelting.SmeltingResult;

import java.util.Optional;
import java.util.OptionalInt;

final class LanternSmeltingRecipe extends LanternRecipe implements ISmeltingRecipe {

    private final ItemStackSnapshot exemplaryIngredient;
    private final IIngredient ingredient;
    final ISmeltingResultProvider resultProvider;
    final ISmeltingTimeProvider smeltingTimeProvider;

    LanternSmeltingRecipe(CatalogKey key,
            ItemStackSnapshot exemplaryResult, ItemStackSnapshot exemplaryIngredient,
            IIngredient ingredient, ISmeltingResultProvider resultProvider,
            ISmeltingTimeProvider smeltingTimeProvider) {
        super(key, exemplaryResult);
        this.exemplaryIngredient = exemplaryIngredient;
        this.ingredient = ingredient;
        this.resultProvider = resultProvider;
        this.smeltingTimeProvider = smeltingTimeProvider;
    }

    @Override
    public ItemStackSnapshot getExemplaryIngredient() {
        return this.exemplaryIngredient;
    }

    @Override
    public boolean isValid(ItemStackSnapshot ingredient) {
        return this.ingredient.test(ingredient.createStack());
    }

    @Override
    public Optional<SmeltingResult> getResult(ItemStackSnapshot ingredient) {
        final ItemStack itemStack = ingredient.createStack();
        return this.ingredient.test(itemStack) ? Optional.of(this.resultProvider.get(itemStack)) : Optional.empty();
    }

    @Override
    public IIngredient getIngredient() {
        return this.ingredient;
    }

    @Override
    public OptionalInt getSmeltTime(ItemStackSnapshot input) {
        return isValid(input) ? OptionalInt.of(this.smeltingTimeProvider.get(input)) : OptionalInt.empty();
    }

    @Override
    public OptionalInt getSmeltTime(ItemStack input) {
        return getSmeltTime(input.createSnapshot());
    }
}
