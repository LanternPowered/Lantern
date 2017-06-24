package org.lanternpowered.server.item.recipe.crafting;

import com.google.common.collect.Multimap;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.recipe.Recipe;
import org.spongepowered.api.item.recipe.crafting.Ingredient;

import java.util.Collection;
import java.util.Collections;
import java.util.Optional;

/**
 * A collection with all the {@link ItemStack}s used in a {@link Recipe} mapped
 * by it's registered {@link Ingredient}. Multiple {@link ItemStack}s may be
 * returned for one {@link Ingredient}.
 */
public class IngredientList {

    private final Multimap<Ingredient, ItemStack> ingredientItems;

    IngredientList(Multimap<Ingredient, ItemStack> ingredientItems) {
        this.ingredientItems = ingredientItems;
    }

    /**
     * Gets the used {@link ItemStack} for the provided
     * {@link Ingredient}, if present.
     *
     * @param ingredient The ingredient
     * @return The item stack
     */
    public Optional<ItemStack> getOne(Ingredient ingredient) {
        final Collection<ItemStack> itemStacks = this.ingredientItems.get(ingredient);
        return itemStacks.isEmpty() ? Optional.empty() : Optional.of(itemStacks.iterator().next());
    }

    /**
     * Gets the used {@link ItemStack}s for the provided
     * {@link Ingredient}, if present.
     *
     * @param ingredient The ingredient
     * @return The item stacks
     */
    public Collection<ItemStack> getAll(Ingredient ingredient) {
        final Collection<ItemStack> itemStacks = this.ingredientItems.get(ingredient);
        return itemStacks.isEmpty() ? Collections.emptyList() : Collections.unmodifiableCollection(itemStacks);
    }

    @SuppressWarnings("ConstantConditions")
    static final IngredientList EMPTY = new IngredientList(null) {

        @Override
        public Optional<ItemStack> getOne(Ingredient ingredient) {
            return Optional.empty();
        }

        @Override
        public Collection<ItemStack> getAll(Ingredient ingredient) {
            return Collections.emptyList();
        }
    };
}
