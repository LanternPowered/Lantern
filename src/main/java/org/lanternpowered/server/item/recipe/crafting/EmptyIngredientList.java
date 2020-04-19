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
import org.spongepowered.api.item.recipe.crafting.Ingredient;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

final class EmptyIngredientList implements IngredientList {

    static final EmptyIngredientList INSTANCE = new EmptyIngredientList();

    @Override
    public Optional<ItemStack> getOne(Ingredient ingredient) {
        return Optional.empty();
    }

    @Override
    public Collection<ItemStack> getAll(Ingredient ingredient) {
        return Collections.emptyList();
    }
}
