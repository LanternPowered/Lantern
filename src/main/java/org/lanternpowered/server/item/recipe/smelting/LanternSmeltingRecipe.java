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
package org.lanternpowered.server.item.recipe.smelting;

import org.lanternpowered.server.item.recipe.LanternRecipe;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipe;
import org.spongepowered.api.item.recipe.smelting.SmeltingResult;

import java.util.Optional;

final class LanternSmeltingRecipe extends LanternRecipe implements SmeltingRecipe {

    private final ItemStackSnapshot exemplaryIngredient;
    private final Ingredient ingredient;
    private final ISmeltingResultProvider resultProvider;

    LanternSmeltingRecipe(String pluginId, String name,
            ItemStackSnapshot exemplaryResult, ItemStackSnapshot exemplaryIngredient,
            Ingredient ingredient, ISmeltingResultProvider resultProvider) {
        super(pluginId, name, exemplaryResult);
        this.exemplaryIngredient = exemplaryIngredient;
        this.ingredient = ingredient;
        this.resultProvider = resultProvider;
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
}
