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

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.type.item.ItemRegistryModule;
import org.lanternpowered.server.item.recipe.IIngredient;
import org.lanternpowered.server.item.recipe.LanternRecipeRegistryModule;
import org.lanternpowered.server.util.ReflectionHelper;
import org.lanternpowered.server.util.UncheckedThrowables;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.registry.RegistrationPhase;
import org.spongepowered.api.registry.util.DelayedRegistration;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

@RegistrationDependency({ ItemRegistryModule.class })
public class LanternCraftingRecipeRegistryModule extends LanternRecipeRegistryModule<CraftingRecipe> {

    public LanternCraftingRecipeRegistryModule() {
        super(CraftingRecipe.class);
    }

    @DelayedRegistration(RegistrationPhase.POST_INIT)
    @Override
    public void registerDefaults() {
        try {
            ReflectionHelper.setField(Ingredient.class.getField("NONE"), null,
                    IIngredient.builder().with(ItemStack::isEmpty).withDisplay(ItemTypes.AIR).build());
        } catch (Throwable e) {
            throw UncheckedThrowables.throwUnchecked(e);
        }
        PluginContainer plugin = Lantern.getMinecraftPlugin();
        register(ICraftingRecipe.shapedBuilder()
                .aisle("x", "x")
                .where('x', Ingredient.of(ItemTypes.OAK_PLANKS))
                .result(ItemStack.of(ItemTypes.STICK, 4))
                .build("stick", plugin));

        // Extra, testing stuff
        plugin = Lantern.getImplementationPlugin();
        ItemStack result = ItemStack.of(ItemTypes.GOLD_NUGGET, 4);
        result.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, "€1"));
        register(ICraftingRecipe.shapedBuilder()
                .aisle("x", "y")
                .where('x', Ingredient.of(ItemTypes.GOLD_INGOT))
                .where('y', IIngredient.builder().with(ItemTypes.LAVA_BUCKET).withRemaining(ItemTypes.BUCKET).build())
                .result(result)
                .build("one_euro", plugin));
        // Two sticks?
        register(ICraftingRecipe.shapelessBuilder()
                .addIngredients(Ingredient.of(ItemTypes.STICK), 2)
                .result(ItemStack.of(ItemTypes.STICK, 2))
                .build("two_sticks", plugin));
        // Two buckets?
        result = ItemStack.of(ItemTypes.LAVA_BUCKET, 1);
        result.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, "¿Compressed Lava?"));
        register(ICraftingRecipe.shapelessBuilder()
                .addIngredient(Ingredient.of(ItemTypes.LAVA_BUCKET))
                .addIngredient(IIngredient.builder().with(ItemTypes.LAVA_BUCKET).withRemaining(ItemTypes.BUCKET).build())
                .result(result)
                .build("compressed_lava", plugin));
    }
}
