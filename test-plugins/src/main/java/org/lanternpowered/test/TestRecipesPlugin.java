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
package org.lanternpowered.test;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.GameRegistry;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipe;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id = "test_recipes", name = "Test Recipes", authors = "Cybermaxke", version = "1.0.0")
public class TestRecipesPlugin {

    @Inject
    private Logger logger;

    @Listener
    public void onStart(GameInitializationEvent event) {
        this.logger.info("Test Recipes plugin enabled!");

        final GameRegistry gameRegistry = Sponge.getGame().getRegistry();
        gameRegistry.getCraftingRecipeRegistry().register(CraftingRecipe.shapedBuilder()
                .aisle("xy", "yx")
                .where('x', Ingredient.of(ItemTypes.APPLE))
                .where('y', Ingredient.of(ItemTypes.GOLD_NUGGET))
                .result(ItemStack.of(ItemTypes.GOLDEN_APPLE, 2))
                .build("golden_apples", this));
        gameRegistry.getSmeltingRecipeRegistry().register(SmeltingRecipe.builder()
                .ingredient(ItemTypes.GOLDEN_APPLE)
                .result(ItemStack.of(ItemTypes.GOLD_NUGGET, 1))
                .build());
    }
}
