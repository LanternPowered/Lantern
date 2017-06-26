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
package org.lanternpowered.server.item.recipe.smelting;

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.game.registry.type.item.ItemRegistryModule;
import org.lanternpowered.server.item.recipe.IIngredient;
import org.lanternpowered.server.item.recipe.LanternRecipeRegistryModule;
import org.lanternpowered.server.item.recipe.crafting.LanternCraftingRecipeRegistryModule;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.plugin.PluginContainer;
import org.spongepowered.api.registry.RegistrationPhase;
import org.spongepowered.api.registry.util.DelayedRegistration;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

@RegistrationDependency({ ItemRegistryModule.class, LanternCraftingRecipeRegistryModule.class })
public class LanternSmeltingRecipeRegistryModule extends LanternRecipeRegistryModule<ISmeltingRecipe> {

    public LanternSmeltingRecipeRegistryModule() {
        super(null);
    }

    @DelayedRegistration(RegistrationPhase.POST_INIT)
    @Override
    public void registerDefaults() {
        PluginContainer plugin = Lantern.getMinecraftPlugin();
        register(ISmeltingRecipe.builder()
                .ingredient(ItemTypes.COBBLESTONE)
                .result(ItemStack.of(ItemTypes.STONE, 1))
                .build("stone", plugin));

        final ItemStack result = ItemStack.of(ItemTypes.IRON_INGOT, 1);
        result.offer(Keys.DISPLAY_NAME, Text.of(TextColors.RESET, "Steel Ingot"));
        register(ISmeltingRecipe.builder()
                .ingredient(IIngredient.builder().with(ItemTypes.IRON_INGOT).withQuantity(2).build(),
                        ItemStack.of(ItemTypes.IRON_INGOT, 1).createSnapshot())
                .result(result)
                .build("steel_ingot", plugin));
    }
}
