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
import org.lanternpowered.server.item.recipe.RecipeBookState;
import org.lanternpowered.server.network.message.Message;

public final class MessagePlayInRecipeBookStates implements Message {

    private final RecipeBookState craftingRecipeBookState;
    private final RecipeBookState smeltingRecipeBookState;

    public MessagePlayInRecipeBookStates(RecipeBookState craftingRecipeBookState,
            RecipeBookState smeltingRecipeBookState) {
        this.craftingRecipeBookState = craftingRecipeBookState;
        this.smeltingRecipeBookState = smeltingRecipeBookState;
    }

    public RecipeBookState getCraftingRecipeBookState() {
        return this.craftingRecipeBookState;
    }

    public RecipeBookState getSmeltingRecipeBookState() {
        return this.smeltingRecipeBookState;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this)
                .add("crafting", this.craftingRecipeBookState)
                .add("smelting", this.smeltingRecipeBookState)
                .toString();
    }
}
