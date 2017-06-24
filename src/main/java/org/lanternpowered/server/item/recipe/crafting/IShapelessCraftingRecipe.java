package org.lanternpowered.server.item.recipe.crafting;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.item.recipe.crafting.ShapelessCraftingRecipe;

import javax.annotation.Nullable;

public interface IShapelessCraftingRecipe extends ShapelessCraftingRecipe, ICraftingRecipe {

    interface Builder extends ShapelessCraftingRecipe.Builder {

        @Override
        Builder.ResultStep addIngredient(Ingredient ingredient);

        interface ResultStep extends Builder, ShapelessCraftingRecipe.Builder.ResultStep {

            /**
             * Sets the {@link ICraftingResultProvider}.
             *
             * @param craftingResultProvider The crafting result provider
             * @return This builder, for chaining
             */
            Builder.EndStep result(ICraftingResultProvider craftingResultProvider);

            @Override
            Builder.EndStep result(ItemStackSnapshot result);

            @Override
            Builder.EndStep result(ItemStack result);
        }

        interface EndStep extends Builder, ShapelessCraftingRecipe.Builder.EndStep {

            @Override
            Builder.EndStep group(@Nullable String name);

            @Override
            IShapelessCraftingRecipe build(String id, Object plugin);
        }
    }
}
