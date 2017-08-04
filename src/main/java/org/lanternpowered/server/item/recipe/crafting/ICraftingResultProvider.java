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
