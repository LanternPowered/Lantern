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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.Conditions.checkPlugin;

import com.google.common.collect.ImmutableList;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.item.recipe.crafting.ShapelessCraftingRecipe;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nullable;

@SuppressWarnings("ConstantConditions")
public class LanternShapelessCraftingRecipeBuilder implements IShapelessCraftingRecipe.Builder.EndStep, IShapelessCraftingRecipe.Builder.ResultStep {

    @Nullable private ICraftingResultProvider resultProvider;
    private final List<Ingredient> ingredients = new ArrayList<>();
    @Nullable private String groupName = "";

    @Override
    public IShapelessCraftingRecipe.Builder.EndStep group(@Nullable String name) {
        this.groupName = name == null ? "" : name;
        return this;
    }

    @Override
    public IShapelessCraftingRecipe.Builder from(ShapelessCraftingRecipe value) {
        this.resultProvider = ((LanternShapelessCraftingRecipe) value).resultProvider;
        this.ingredients.clear();
        this.ingredients.addAll(value.getIngredientPredicates());
        this.groupName = value.getGroup().orElse(null);
        return this;
    }

    @Override
    public IShapelessCraftingRecipe.Builder reset() {
        this.resultProvider = null;
        this.ingredients.clear();
        this.groupName = null;
        return this;
    }

    @Override
    public IShapelessCraftingRecipe.Builder.ResultStep addIngredient(Ingredient ingredient) {
        checkNotNull(ingredient, "ingredient");
        this.ingredients.add(ingredient);
        return this;
    }

    @Override
    public IShapelessCraftingRecipe.Builder.ResultStep addIngredients(Ingredient ingredient, int times) {
        checkNotNull(ingredient, "ingredient");
        for (int i = 0; i < times; i++) {
            this.ingredients.add(ingredient);
        }
        return this;
    }

    @Override
    public IShapelessCraftingRecipe.Builder.EndStep result(ICraftingResultProvider resultProvider) {
        checkNotNull(resultProvider, "resultProvider");
        this.resultProvider = resultProvider;
        return this;
    }

    @Override
    public IShapelessCraftingRecipe.Builder.EndStep result(ItemStackSnapshot result) {
        checkNotNull(result, "result");
        checkArgument(!result.isEmpty(), "The result must not be empty.");
        return result(new ConstantCraftingResultProvider(result));
    }

    @Override
    public IShapelessCraftingRecipe.Builder.EndStep result(ItemStack result) {
        checkNotNull(result, "result");
        checkArgument(!result.isEmpty(), "The result must not be empty.");
        return result(new ConstantCraftingResultProvider(result.createSnapshot()));
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public IShapelessCraftingRecipe build(String id, Object plugin) {
        checkState(this.resultProvider != null, "The result provider is not set.");
        checkState(!this.ingredients.isEmpty(), "The ingredients are not set.");
        checkNotNull(id, "id");
        checkNotNull(id, "plugin");

        final PluginContainer container = checkPlugin(plugin, "plugin");
        final int index = id.indexOf(':');
        if (index != -1) {
            final String pluginId = id.substring(0, index);
            checkState(pluginId.equals(container.getId()), "Plugin ids mismatch, "
                    + "found %s in the id but got %s from the container", pluginId, container.getId());
            id = id.substring(index + 1);
        }

        final ItemStackSnapshot exemplary = this.resultProvider.getSnapshot(
                EmptyCraftingMatrix.INSTANCE, EmptyIngredientList.INSTANCE);
        return new LanternShapelessCraftingRecipe(container.getId(), id, exemplary,
                this.groupName, this.resultProvider, ImmutableList.copyOf(this.ingredients));
    }
}
