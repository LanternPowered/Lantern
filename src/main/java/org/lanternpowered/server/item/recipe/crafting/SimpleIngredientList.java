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

import com.google.common.collect.Multimap;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.recipe.crafting.Ingredient;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

final class SimpleIngredientList implements IngredientList {

    private final Multimap<Ingredient, ItemStack> ingredientItems;

    SimpleIngredientList(Multimap<Ingredient, ItemStack> ingredientItems) {
        this.ingredientItems = ingredientItems;
    }

    @Override
    public Optional<ItemStack> getOne(Ingredient ingredient) {
        final Collection<ItemStack> itemStacks = this.ingredientItems.get(ingredient);
        return itemStacks.isEmpty() ? Optional.empty() : Optional.of(itemStacks.iterator().next());
    }

    @Override
    public Collection<ItemStack> getAll(Ingredient ingredient) {
        final Collection<ItemStack> itemStacks = this.ingredientItems.get(ingredient);
        return itemStacks.isEmpty() ? Collections.emptyList() : Collections.unmodifiableCollection(itemStacks);
    }
}
