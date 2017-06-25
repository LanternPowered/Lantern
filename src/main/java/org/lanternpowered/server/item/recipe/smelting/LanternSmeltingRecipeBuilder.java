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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.Conditions.checkPlugin;

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.item.recipe.IIngredient;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipe;
import org.spongepowered.api.item.recipe.smelting.SmeltingResult;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Predicate;

@SuppressWarnings("ConstantConditions")
public class LanternSmeltingRecipeBuilder implements ISmeltingRecipe.Builder,
        ISmeltingRecipe.Builder.EndStep, ISmeltingRecipe.Builder.ResultStep {

    private static final ISmeltingTimeProvider DEFAULT_SMELTING_TIME_PROVIDER = new ConstantSmeltingTimeProvider(200);

    private ISmeltingResultProvider resultProvider;
    private ISmeltingTimeProvider smeltingTimeProvider;
    private ItemStackSnapshot result;
    private ItemStackSnapshot exemplaryIngredient;
    private Ingredient ingredient;
    private double experience;

    @Override
    public ISmeltingRecipe.Builder from(SmeltingRecipe value) {
        checkNotNull(value, "value");
        this.resultProvider = ((LanternSmeltingRecipe) value).resultProvider;
        this.smeltingTimeProvider = ((LanternSmeltingRecipe) value).smeltingTimeProvider;
        this.exemplaryIngredient = value.getExemplaryIngredient();
        this.experience = value.getResult(this.exemplaryIngredient).get().getExperience();
        this.ingredient = ((LanternSmeltingRecipe) value).ingredient;
        this.result = null;
        return this;
    }

    @Override
    public ISmeltingRecipe.Builder reset() {
        this.resultProvider = null;
        this.smeltingTimeProvider = null;
        this.result = null;
        this.exemplaryIngredient = null;
        this.ingredient = null;
        this.experience = 0;
        return this;
    }

    @Override
    public ISmeltingRecipe.Builder.ResultStep ingredient(Ingredient ingredient, ItemStackSnapshot exemplaryIngredient) {
        checkNotNull(ingredient, "ingredient");
        checkNotNull(exemplaryIngredient, "exemplaryIngredient");
        this.ingredient = ingredient;
        this.exemplaryIngredient = exemplaryIngredient;
        return this;
    }

    @Override
    public ISmeltingRecipe.Builder.ResultStep ingredient(Predicate<ItemStackSnapshot> ingredientPredicate, ItemStackSnapshot exemplaryIngredient) {
        checkNotNull(ingredientPredicate, "ingredientPredicate");
        checkNotNull(exemplaryIngredient, "exemplaryIngredient");
        this.ingredient = IIngredient.builder()
                .with(itemStack -> ingredientPredicate.test(itemStack.createSnapshot()))
                .build();
        this.exemplaryIngredient = exemplaryIngredient;
        return this;
    }

    @Override
    public ISmeltingRecipe.Builder.ResultStep ingredient(ItemStackSnapshot ingredient) {
        checkNotNull(ingredient, "ingredient");
        this.ingredient = IIngredient.builder()
                .with(ingredient)
                .build();
        this.exemplaryIngredient = ingredient;
        return this;
    }

    @Override
    public ISmeltingRecipe.Builder.ResultStep ingredient(ItemStack ingredient) {
        checkNotNull(ingredient, "ingredient");
        this.ingredient = IIngredient.builder()
                .with(ingredient)
                .build();
        this.exemplaryIngredient = ingredient.createSnapshot();
        return this;
    }

    @Override
    public ISmeltingRecipe.Builder.ResultStep ingredient(ItemType ingredient) {
        checkNotNull(ingredient, "ingredient");
        this.ingredient = IIngredient.builder()
                .with(ingredient)
                .build();
        this.exemplaryIngredient = ItemStack.of(ingredient, 1).createSnapshot();
        return this;
    }

    @Override
    public ISmeltingRecipe.Builder.EndStep result(ISmeltingResultProvider resultProvider) {
        checkNotNull(resultProvider, "resultProvider");
        this.resultProvider = resultProvider;
        this.experience = 0;
        this.result = null;
        return this;
    }

    @Override
    public ISmeltingRecipe.Builder.EndStep result(SmeltingResult result) {
        checkNotNull(result, "result");
        this.resultProvider = new ConstantSmeltingResultProvider(result);
        this.experience = 0;
        this.result = null;
        return this;
    }

    @Override
    public ISmeltingRecipe.Builder.EndStep result(ItemStackSnapshot result) {
        checkNotNull(result, "result");
        this.resultProvider = null;
        this.result = result;
        this.experience = 0;
        return this;
    }

    @Override
    public ISmeltingRecipe.Builder.EndStep result(ItemStack result) {
        checkNotNull(result, "result");
        return result(result.createSnapshot());
    }

    @Override
    public ISmeltingRecipe.Builder.EndStep smeltTime(ISmeltingTimeProvider smeltingTimeProvider) {
        checkNotNull(smeltingTimeProvider, "smeltingTimeProvider");
        this.smeltingTimeProvider = smeltingTimeProvider;
        return this;
    }

    @Override
    public ISmeltingRecipe.Builder.EndStep smeltTime(int smeltingTime) {
        checkArgument(smeltingTime > 0, "The smelting time must be greater then 0");
        this.smeltingTimeProvider = new ConstantSmeltingTimeProvider(smeltingTime);
        return this;
    }

    @Override
    public ISmeltingRecipe.Builder.EndStep experience(double experience) {
        this.experience = experience;
        return this;
    }

    private static final Map<String, Integer> idCounters = new HashMap<>();

    @Override
    public ISmeltingRecipe build() {
        check();

        // Attempt to generate a id for the smelting recipe
        final String ingredient = this.exemplaryIngredient.getType().getId();
        final ItemStackSnapshot exemplaryResult = getResultProvider().get(this.exemplaryIngredient).getResult();
        final String result = exemplaryResult.getType().getId();

        final String id = ingredient + "_to_" + result;
        final int count = idCounters.computeIfAbsent(id, s -> 0) + 1;
        idCounters.put(id, count);

        return build0(id + "_" + count, Lantern.getGame().getImplementationPlugin());
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public ISmeltingRecipe build(String id, Object plugin) {
        check();

        checkNotNull(id, "id");
        checkNotNull(plugin, "plugin");

        final PluginContainer container = checkPlugin(plugin, "plugin");
        final int index = id.indexOf(':');
        if (index != -1) {
            final String pluginId = id.substring(0, index);
            checkState(pluginId.equals(container.getId()), "Plugin ids mismatch, "
                    + "found %s in the id but got %s from the container", pluginId, container.getId());
            id = id.substring(index + 1);
        }

        return build0(id, container);
    }

    private void check() {
        checkState(this.resultProvider != null || this.result != null, "The result provider is not set.");
        checkState(this.ingredient != null, "The ingredient is not set.");
    }

    private ISmeltingResultProvider getResultProvider() {
        ISmeltingResultProvider resultProvider = this.resultProvider;
        if (resultProvider == null) {
            resultProvider = new ConstantSmeltingResultProvider(new SmeltingResult(this.result, this.experience));
        }
        return resultProvider;
    }

    private ISmeltingRecipe build0(String id, PluginContainer plugin) {
        ISmeltingResultProvider resultProvider = getResultProvider();
        final ItemStackSnapshot exemplaryResult = resultProvider.get(this.exemplaryIngredient).getResult();
        ISmeltingTimeProvider smeltingTimeProvider = this.smeltingTimeProvider;
        if (smeltingTimeProvider == null) {
            smeltingTimeProvider = DEFAULT_SMELTING_TIME_PROVIDER;
        }
        return new LanternSmeltingRecipe(plugin.getId(), id, exemplaryResult,
                this.exemplaryIngredient, this.ingredient, resultProvider, smeltingTimeProvider);
    }
}
