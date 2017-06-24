package org.lanternpowered.server.item.recipe;

import org.spongepowered.api.GameDictionary;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;

import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * An extension of {@link Ingredient} that allows a
 * remaining item to be provided.
 */
public interface IIngredient extends Ingredient {

    /**
     * Creates a new {@link Builder} to build an {@link IIngredient}.
     *
     * @return The new builder
     */
    static Builder builder() {
        return Sponge.getRegistry().createBuilder(Builder.class);
    }

    /**
     * Gets the remaining {@link ItemStack}, if present.
     *
     * @return The remaining item
     */
    Optional<ItemStack> getRemainingItem(ItemStack itemStack);

    /**
     * A builder to construct {@link IIngredient}s.
     */
    interface Builder extends Ingredient.Builder {

        /**
         * Applies the {@link ItemType} that should be returned when
         * a recipe is used.
         *
         * @param type The item type
         * @return This builder, for chaining
         */
        Builder withRemaining(ItemType type);

        /**
         * Applies the {@link ItemStack} that should be returned when
         * a recipe is used.
         *
         * @param item The item stack
         * @return This builder, for chaining
         */
        Builder withRemaining(ItemStack item);

        /**
         * Applies the {@link ItemStackSnapshot} that should be returned when
         * a recipe is used.
         *
         * @param item The item stack snapshot
         * @return This builder, for chaining
         */
        Builder withRemaining(ItemStackSnapshot item);

        /**
         * Applies the {@link Function} that will generate the returned
         * {@link ItemStack} based on the input {@link ItemStack}.
         *
         * @param provider The provider
         * @return This builder, for chaining
         */
        Builder withRemaining(Function<ItemStack, ItemStack> provider);

        @Override
        Builder with(Predicate<ItemStack> predicate);

        @Override
        Builder with(GameDictionary.Entry entry);

        @Override
        Builder with(ItemType... types);

        @Override
        Builder with(ItemStack... items);

        @Override
        Builder with(ItemStackSnapshot... items);

        @Override
        Builder withDisplay(ItemType... types);

        @Override
        Builder withDisplay(ItemStack... items);

        @Override
        Builder withDisplay(ItemStackSnapshot... items);

        @Override
        IIngredient build();
    }
}
