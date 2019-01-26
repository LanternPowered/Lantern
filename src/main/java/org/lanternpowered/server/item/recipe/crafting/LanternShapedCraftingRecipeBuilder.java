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

import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import org.lanternpowered.server.catalog.AbstractCatalogBuilder;
import org.lanternpowered.server.item.recipe.IIngredient;
import org.spongepowered.api.CatalogKey;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.item.recipe.crafting.ShapedCraftingRecipe;
import org.spongepowered.api.text.translation.Translation;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public final class LanternShapedCraftingRecipeBuilder extends AbstractCatalogBuilder<ShapedCraftingRecipe, ShapedCraftingRecipe.Builder> implements
        IShapedCraftingRecipe.Builder,
        IShapedCraftingRecipe.Builder.AisleStep.ResultStep,
        IShapedCraftingRecipe.Builder.RowsStep.ResultStep,
        IShapedCraftingRecipe.Builder.EndStep {

    private final List<String> aisle = new ArrayList<>();
    private final Map<Character, Ingredient> ingredientMap = new Char2ObjectArrayMap<>();
    @Nullable private ICraftingResultProvider resultProvider;
    @Nullable private String groupName;

    @Override
    public EndStep group(@Nullable String name) {
        this.groupName = name;
        return this;
    }

    @Override
    public AisleStep aisle(String... aisle) {
        checkNotNull(aisle, "aisle");
        this.aisle.clear();
        this.ingredientMap.clear();
        Collections.addAll(this.aisle, aisle);
        return this;
    }

    @Override
    public AisleStep.ResultStep where(char symbol, @Nullable Ingredient ingredient) throws IllegalArgumentException {
        if (this.aisle.stream().noneMatch(row -> row.indexOf(symbol) >= 0)) {
            throw new IllegalArgumentException("The symbol '" + symbol + "' is not defined in the aisle pattern.");
        }
        this.ingredientMap.put(symbol, ingredient == null ? Ingredient.NONE : ingredient);
        return this;
    }

    @Override
    public AisleStep.ResultStep where(Map<Character, Ingredient> ingredientMap) throws IllegalArgumentException {
        for (Map.Entry<Character, Ingredient> entry : ingredientMap.entrySet()) {
            where(entry.getKey(), entry.getValue());
        }
        return this;
    }

    @Override
    public RowsStep rows() {
        this.aisle.clear();
        this.ingredientMap.clear();
        return this;
    }

    @Override
    public RowsStep.ResultStep row(Ingredient... ingredients) {
        return row(0, ingredients);
    }

    @Override
    public RowsStep.ResultStep row(int skip, Ingredient... ingredients) {
        int columns = ingredients.length + skip;
        if (!this.aisle.isEmpty()) {
            checkState(this.aisle.get(0).length() == columns, "The rows have an inconsistent width.");
        }
        final StringBuilder row = new StringBuilder();
        for (int i = 0; i < skip; i++) {
            row.append(" ");
        }
        int key = 'a' + columns * this.aisle.size();
        for (Ingredient ingredient : ingredients) {
            key++;
            final char character = (char) key;
            row.append(character);
            this.ingredientMap.put(character, ingredient);
        }
        this.aisle.add(row.toString());
        return this;
    }

    @Override
    public EndStep result(ICraftingResultProvider resultProvider) {
        checkNotNull(resultProvider, "resultProvider");
        this.resultProvider = resultProvider;
        return this;
    }

    @Override
    public EndStep result(ItemStackSnapshot result) {
        checkNotNull(result, "result");
        checkArgument(!result.isEmpty(), "The result must not be empty.");
        return result(new ConstantCraftingResultProvider(result));
    }

    @Override
    public EndStep result(ItemStack result) {
        checkNotNull(result, "result");
        checkArgument(!result.isEmpty(), "The result must not be empty.");
        return result(result.createSnapshot());
    }

    @Override
    public EndStep id(String id) {
        super.id(id);
        return this;
    }

    @Override
    public EndStep name(String name) {
        super.name(name);
        return this;
    }

    @Override
    public EndStep name(Translation name) {
        super.name(name);
        return this;
    }

    @Override
    public EndStep key(CatalogKey key) {
        super.key(key);
        return this;
    }

    @Override
    public IShapedCraftingRecipe build() {
        return (IShapedCraftingRecipe) super.build();
    }

    @Override
    protected ShapedCraftingRecipe build(CatalogKey key, Translation name) {
        checkState(!this.aisle.isEmpty(), "aisle has not been set");
        checkState(!this.ingredientMap.isEmpty(), "no ingredients set");
        checkState(this.resultProvider != null, "no result provider");

        final int h = this.aisle.size();
        final int w = this.aisle.get(0).length();
        checkState(w > 0, "The aisle cannot be empty.");

        final IIngredient[][] ingredients = new IIngredient[w][h];

        for (int j = 0; j < h; j++) {
            final String s = this.aisle.get(j);
            checkState(s.length() == w, "The aisle has an inconsistent width.");
            for (int i = 0; i < w; i++) {
                final char c = s.charAt(i);
                final Ingredient ingredient;
                if (c == ' ') {
                    ingredient = Ingredient.NONE;
                } else {
                    ingredient = this.ingredientMap.get(c);
                    checkState(ingredient != null, "No ingredient is present for the character: %s", c);
                }
                ingredients[i][j] = (IIngredient) ingredient;
            }
        }

        final ItemStackSnapshot exemplary = this.resultProvider.getSnapshot(
                EmptyCraftingMatrix.INSTANCE, EmptyIngredientList.INSTANCE);
        return new LanternShapedCraftingRecipe(key, exemplary, this.groupName, this.resultProvider, ingredients);
    }

    @Override
    public IShapedCraftingRecipe.Builder from(ShapedCraftingRecipe value) {
        this.aisle.clear();
        this.ingredientMap.clear();
        this.groupName = value.getGroup().orElse(null);
        for (int y = 0; y < value.getHeight(); y++) {
            final StringBuilder row = new StringBuilder();
            for (int x = 0; x < value.getWidth(); x++) {
                final char symbol = (char) ('a' + x + y * value.getWidth());
                row.append(symbol);
                final Ingredient ingredient = value.getIngredient(x, y);
                this.ingredientMap.put(symbol, ingredient);
            }
            this.aisle.add(row.toString());
        }
        this.resultProvider = ((LanternShapedCraftingRecipe) value).resultProvider;
        return this;
    }

    @Override
    public IShapedCraftingRecipe.Builder reset() {
        this.aisle.clear();
        this.ingredientMap.clear();
        this.resultProvider = null;
        this.groupName = null;
        super.reset();
        return this;
    }
}
