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

import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.item.recipe.LanternRecipe;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.CraftingResult;
import org.spongepowered.api.world.World;

import java.util.List;
import java.util.Optional;

import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
abstract class LanternCraftingRecipe extends LanternRecipe implements ICraftingRecipe {

    @Nullable private final String group;

    LanternCraftingRecipe(String pluginId, String name,
            ItemStackSnapshot exemplaryResult, @Nullable String group) {
        super(pluginId, name, exemplaryResult);
        this.group = group;
    }

    @Override
    public Optional<String> getGroup() {
        return Optional.ofNullable(this.group);
    }

    @Override
    public boolean isValid(CraftingMatrix craftingMatrix, World world) {
        return match(craftingMatrix, false, false) != null;
    }

    @Override
    public ItemStackSnapshot getResult(CraftingMatrix craftingMatrix) {
        final Result result = match(craftingMatrix, true, false);
        checkState(result != null, "isValid is false");
        return result.resultItem.createSnapshot();
    }

    @Override
    public List<ItemStackSnapshot> getRemainingItems(CraftingMatrix craftingMatrix) {
        final Result result = match(craftingMatrix, false, true);
        checkState(result != null, "isValid is false");
        return result.remainingItems;
    }

    @Override
    public Optional<CraftingResult> getResult(CraftingMatrix craftingMatrix, @Nullable World world) {
        final Result result = match(craftingMatrix, true, true);
        return result == null ? Optional.empty() : Optional.of(
                new CraftingResult(result.resultItem.createSnapshot(), result.remainingItems));
    }

    static final class Result {

        @Nullable private final ItemStack resultItem;
        @Nullable private final List<ItemStackSnapshot> remainingItems;

        Result(@Nullable ItemStack resultItem, @Nullable List<ItemStackSnapshot> remainingItems) {
            this.remainingItems = remainingItems;
            this.resultItem = resultItem;
        }
    }

    @Nullable
    abstract Result match(CraftingMatrix craftingMatrix, boolean resultItem, boolean remainingItems);
}
