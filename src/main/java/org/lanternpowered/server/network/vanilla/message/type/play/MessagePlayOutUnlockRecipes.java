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
package org.lanternpowered.server.network.vanilla.message.type.play;

import com.google.common.base.MoreObjects;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import org.lanternpowered.server.item.recipe.RecipeBookState;
import org.lanternpowered.server.network.message.Message;

import java.util.List;

public abstract class MessagePlayOutUnlockRecipes implements Message {

    private final RecipeBookState craftingRecipeBookState;
    private final RecipeBookState smeltingRecipeBookState;
    private final List<String> recipeIds;

    private MessagePlayOutUnlockRecipes(
            RecipeBookState craftingRecipeBookState,
            RecipeBookState smeltingRecipeBookState,
            List<String> recipeIds) {
        this.craftingRecipeBookState = craftingRecipeBookState;
        this.smeltingRecipeBookState = smeltingRecipeBookState;
        this.recipeIds = ImmutableList.copyOf(recipeIds);
    }

    public RecipeBookState getCraftingRecipeBookState() {
        return this.craftingRecipeBookState;
    }

    public RecipeBookState getSmeltingRecipeBookState() {
        return this.smeltingRecipeBookState;
    }

    public MoreObjects.ToStringHelper toStringHelper() {
        return MoreObjects.toStringHelper(getClass().getSuperclass().getSimpleName() + "." + getClass().getSimpleName())
                .add("craftingRecipeBookState", this.craftingRecipeBookState)
                .add("smeltingRecipeBookState", this.smeltingRecipeBookState)
                .add("recipeIds", Iterables.toString(this.recipeIds));
    }

    public List<String> getRecipeIds() {
        return this.recipeIds;
    }

    @Override
    public String toString() {
        return toStringHelper().toString();
    }

    public final static class Remove extends MessagePlayOutUnlockRecipes {

        public Remove(
                RecipeBookState craftingRecipeBookState,
                RecipeBookState smeltingRecipeBookState,
                List<String> recipeIds) {
            super(craftingRecipeBookState, smeltingRecipeBookState, recipeIds);
        }
    }

    public final static class Init extends MessagePlayOutUnlockRecipes {

        private final List<String> recipeIdsToBeDisplayed;

        public Init(
                RecipeBookState craftingRecipeBookState,
                RecipeBookState smeltingRecipeBookState,
                List<String> recipeIds, List<String> recipeIdsToBeDisplayed) {
            super(craftingRecipeBookState, smeltingRecipeBookState, recipeIds);
            this.recipeIdsToBeDisplayed = ImmutableList.copyOf(recipeIdsToBeDisplayed);
        }

        public List<String> getRecipeIdsToBeDisplayed() {
            return this.recipeIdsToBeDisplayed;
        }

        @Override
        public MoreObjects.ToStringHelper toStringHelper() {
            return super.toStringHelper()
                    .add("recipeIdsToBeDisplayed", Iterables.toString(this.recipeIdsToBeDisplayed));
        }
    }

    public final static class Add extends MessagePlayOutUnlockRecipes {

        public Add(
                RecipeBookState craftingRecipeBookState,
                RecipeBookState smeltingRecipeBookState,
                List<String> recipeIds) {
            super(craftingRecipeBookState, smeltingRecipeBookState, recipeIds);
        }
    }
}
