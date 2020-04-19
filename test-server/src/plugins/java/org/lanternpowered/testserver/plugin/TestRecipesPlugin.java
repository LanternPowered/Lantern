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
package org.lanternpowered.testserver.plugin;

import com.google.inject.Inject;
import org.slf4j.Logger;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameRegistryEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipe;
import org.spongepowered.api.plugin.Plugin;

@Plugin(id = "test_recipes", name = "Test Recipes", authors = "Cybermaxke", version = "1.0.0")
public class TestRecipesPlugin {

    private final Logger logger;

    @Inject
    public TestRecipesPlugin(Logger logger) {
        this.logger = logger;
    }

    @Listener
    public void onStart(GameInitializationEvent event) {
        this.logger.info("Test Recipes plugin enabled!");
    }

    @Listener
    public void onRegisterCraftingRecipes(GameRegistryEvent.Register<CraftingRecipe> event) {
        event.register(CraftingRecipe.shapedBuilder()
                .aisle("xy", "yx")
                .where('x', Ingredient.of(ItemTypes.APPLE))
                .where('y', Ingredient.of(ItemTypes.GOLD_NUGGET))
                .result(ItemStack.of(ItemTypes.GOLDEN_APPLE, 2))
                .id("golden_apples")
                .build());
    }

    @Listener
    public void onRegisterSmeltingRecipes(GameRegistryEvent.Register<SmeltingRecipe> event) {
        event.register(SmeltingRecipe.builder()
                .ingredient(ItemTypes.GOLDEN_APPLE)
                .result(ItemStack.of(ItemTypes.GOLD_NUGGET, 1))
                .id("smelt_golden_apple")
                .build());
    }
}
