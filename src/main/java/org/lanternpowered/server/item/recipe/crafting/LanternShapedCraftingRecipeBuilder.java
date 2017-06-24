package org.lanternpowered.server.item.recipe.crafting;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.lanternpowered.server.util.Conditions.checkPlugin;

import it.unimi.dsi.fastutil.chars.Char2ObjectArrayMap;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.item.recipe.crafting.ShapedCraftingRecipe;
import org.spongepowered.api.plugin.PluginContainer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;

public final class LanternShapedCraftingRecipeBuilder implements IShapedCraftingRecipe.Builder, IShapedCraftingRecipe.Builder.AisleStep.ResultStep,
        IShapedCraftingRecipe.Builder.RowsStep.ResultStep, IShapedCraftingRecipe.Builder.EndStep {

    private final List<String> aisle = new ArrayList<>();
    private final Map<Character, Ingredient> ingredientMap = new Char2ObjectArrayMap<>();
    @Nullable private ICraftingResultProvider craftingResultProvider;
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
    public EndStep result(ICraftingResultProvider craftingResultProvider) {
        checkNotNull(craftingResultProvider, "craftingResultProvider");
        this.craftingResultProvider = craftingResultProvider;
        return this;
    }

    @Override
    public EndStep result(ItemStackSnapshot result) {
        checkNotNull(result, "result");
        return result(new ConstantCraftingResultProvider(result));
    }

    @Override
    public EndStep result(ItemStack result) {
        checkNotNull(result, "result");
        return result(result.createSnapshot());
    }

    @Override
    public IShapedCraftingRecipe build(String id, Object plugin) {
        checkState(!this.aisle.isEmpty(), "aisle has not been set");
        checkState(!this.ingredientMap.isEmpty(), "no ingredients set");
        checkState(this.craftingResultProvider != null, "no result set");
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

        final int h = this.aisle.size();
        checkState(h > 0, "The aisle cannot be empty.");
        final int w = this.aisle.get(0).length();
        checkState(w > 0, "The aisle cannot be empty.");

        final Ingredient[][] ingredients = new Ingredient[w][h];

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
                ingredients[i][j] = ingredient;
            }
        }

        final ItemStackSnapshot exemplary = this.craftingResultProvider.getSnapshot(CraftingMatrix.EMPTY, IngredientList.EMPTY);
        return new LanternShapedCraftingRecipe(container.getId(), id, exemplary,
                this.groupName, this.craftingResultProvider, ingredients);
    }

    @Override
    public ShapedCraftingRecipe.Builder from(ShapedCraftingRecipe value) {
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
        this.craftingResultProvider = ((LanternShapedCraftingRecipe) value).craftingResultProvider;
        return this;
    }

    @Override
    public ShapedCraftingRecipe.Builder reset() {
        this.aisle.clear();
        this.ingredientMap.clear();
        this.craftingResultProvider = null;
        this.groupName = null;
        return this;
    }
}
