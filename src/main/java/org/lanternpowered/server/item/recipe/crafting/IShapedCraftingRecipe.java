package org.lanternpowered.server.item.recipe.crafting;

import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.item.recipe.crafting.ShapedCraftingRecipe;

import java.util.Map;

import javax.annotation.Nullable;

public interface IShapedCraftingRecipe extends ICraftingRecipe, ShapedCraftingRecipe {

    static IShapedCraftingRecipe.Builder builder() {
        return Sponge.getRegistry().createBuilder(IShapedCraftingRecipe.Builder.class);
    }

    interface Builder extends ShapedCraftingRecipe.Builder {

        @Override
        AisleStep aisle(String... aisle);

        interface AisleStep extends Builder, ShapedCraftingRecipe.Builder.AisleStep {

            interface ResultStep extends Builder.AisleStep, Builder.ResultStep, ShapedCraftingRecipe.Builder.AisleStep.ResultStep {}

            @Override
            ResultStep where(char symbol, @Nullable Ingredient ingredient) throws IllegalArgumentException;

            @Override
            ResultStep where(Map<Character, Ingredient> ingredientMap) throws IllegalArgumentException;
        }

        @Override
        RowsStep rows();

        interface RowsStep extends Builder, ShapedCraftingRecipe.Builder.RowsStep {

            interface ResultStep extends Builder.RowsStep, Builder.ResultStep, ShapedCraftingRecipe.Builder.RowsStep.ResultStep {}

            @Override
            ResultStep row(Ingredient... ingredients);

            @Override
            ResultStep row(int skip, Ingredient... ingredients);
        }

        interface ResultStep extends Builder, ShapedCraftingRecipe.Builder.ResultStep {

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

        interface EndStep extends Builder, ShapedCraftingRecipe.Builder.EndStep {

            @Override
            Builder.EndStep group(@Nullable String name);

            @Override
            IShapedCraftingRecipe build(String id, Object plugin);
        }
    }
}
