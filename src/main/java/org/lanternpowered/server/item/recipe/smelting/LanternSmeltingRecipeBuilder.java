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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.catalog.AbstractCatalogBuilder;
import org.lanternpowered.server.item.recipe.IIngredient;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipe;
import org.spongepowered.api.item.recipe.smelting.SmeltingResult;
import org.spongepowered.api.text.translation.Translation;

import java.util.function.Predicate;

@SuppressWarnings("ConstantConditions")
public class LanternSmeltingRecipeBuilder extends AbstractCatalogBuilder<SmeltingRecipe, SmeltingRecipe.Builder> implements ISmeltingRecipe.Builder,
        ISmeltingRecipe.Builder.EndStep, ISmeltingRecipe.Builder.ResultStep {

    private static final ISmeltingTimeProvider DEFAULT_SMELTING_TIME_PROVIDER = new ConstantSmeltingTimeProvider(200);

    private ISmeltingResultProvider resultProvider;
    private ISmeltingTimeProvider smeltingTimeProvider;
    private ItemStackSnapshot result;
    private ItemStackSnapshot exemplaryIngredient;
    private IIngredient ingredient;
    private double experience;

    @Override
    public ISmeltingRecipe.Builder from(SmeltingRecipe value) {
        checkNotNull(value, "value");
        this.resultProvider = ((LanternSmeltingRecipe) value).resultProvider;
        this.smeltingTimeProvider = ((LanternSmeltingRecipe) value).smeltingTimeProvider;
        this.exemplaryIngredient = value.getExemplaryIngredient();
        this.experience = value.getResult(this.exemplaryIngredient).get().getExperience();
        this.ingredient = ((ISmeltingRecipe) value).getIngredient();
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
        this.ingredient = (IIngredient) ingredient;
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

    @Override
    public ISmeltingRecipe.Builder.EndStep id(String id) {
        super.id(id);
        return this;
    }

    @Override
    public ISmeltingRecipe.Builder.EndStep name(String name) {
        super.name(name);
        return this;
    }

    @Override
    public ISmeltingRecipe.Builder.EndStep name(Translation name) {
        super.name(name);
        return this;
    }

    @Override
    public ISmeltingRecipe.Builder.EndStep key(CatalogKey key) {
        super.key(key);
        return this;
    }

    @Override
    public ISmeltingRecipe build() {
        return (ISmeltingRecipe) super.build();
    }

    @Override
    protected SmeltingRecipe build(CatalogKey key, Translation name) {
        checkState(this.resultProvider != null || this.result != null, "The result provider is not set.");
        checkState(this.ingredient != null, "The ingredient is not set.");
        ISmeltingResultProvider resultProvider = this.resultProvider;
        if (resultProvider == null) {
            resultProvider = new ConstantSmeltingResultProvider(new SmeltingResult(this.result, this.experience));
        }
        final ItemStackSnapshot exemplaryResult = resultProvider.get(this.exemplaryIngredient).getResult();
        ISmeltingTimeProvider smeltingTimeProvider = this.smeltingTimeProvider;
        if (smeltingTimeProvider == null) {
            smeltingTimeProvider = DEFAULT_SMELTING_TIME_PROVIDER;
        }
        return new LanternSmeltingRecipe(key, exemplaryResult,
                this.exemplaryIngredient, this.ingredient, resultProvider, smeltingTimeProvider);
    }
}
