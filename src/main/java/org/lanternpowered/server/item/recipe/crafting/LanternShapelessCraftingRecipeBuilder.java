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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import com.google.common.collect.ImmutableList;
import org.lanternpowered.server.catalog.AbstractCatalogBuilder;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.item.recipe.crafting.ShapelessCraftingRecipe;
import org.spongepowered.api.text.translation.Translation;

import java.util.ArrayList;
import java.util.List;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("ConstantConditions")
public class LanternShapelessCraftingRecipeBuilder extends AbstractCatalogBuilder<ShapelessCraftingRecipe, ShapelessCraftingRecipe.Builder>
        implements IShapelessCraftingRecipe.Builder.EndStep, IShapelessCraftingRecipe.Builder.ResultStep {

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
            addIngredient(ingredient);
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

    @Override
    public IShapelessCraftingRecipe.Builder.EndStep id(String id) {
        super.id(id);
        return this;
    }

    @Override
    public IShapelessCraftingRecipe.Builder.EndStep name(String name) {
        super.name(name);
        return this;
    }

    @Override
    public IShapelessCraftingRecipe.Builder.EndStep name(Translation name) {
        super.name(name);
        return this;
    }

    @Override
    public IShapelessCraftingRecipe.Builder.EndStep key(ResourceKey key) {
        super.key(key);
        return this;
    }

    @Override
    public IShapelessCraftingRecipe build() {
        return (IShapelessCraftingRecipe) super.build();
    }

    @Override
    protected ShapelessCraftingRecipe build(ResourceKey key, Translation name) {
        checkState(this.resultProvider != null, "The result provider is not set.");
        checkState(!this.ingredients.isEmpty(), "The ingredients are not set.");

        final ItemStackSnapshot exemplary = this.resultProvider.getSnapshot(
                EmptyCraftingMatrix.INSTANCE, EmptyIngredientList.INSTANCE);
        return new LanternShapelessCraftingRecipe(key, exemplary,
                this.groupName, this.resultProvider, ImmutableList.copyOf(this.ingredients));
    }
}
