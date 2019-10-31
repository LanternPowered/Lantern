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

import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.item.ItemTypeRegistry;
import org.lanternpowered.server.item.recipe.AbstractRecipeRegistry;
import org.lanternpowered.server.item.recipe.IIngredient;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipe;
import org.spongepowered.api.item.recipe.smelting.SmeltingRecipeRegistry;
import org.spongepowered.api.item.recipe.smelting.SmeltingResult;
import org.spongepowered.api.registry.RegistrationPhase;
import org.spongepowered.api.registry.util.DelayedRegistration;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;

import java.util.Optional;

import static com.google.common.base.Preconditions.checkNotNull;

@RegistrationDependency(ItemTypeRegistry.class)
public class LanternSmeltingRecipeRegistry extends AbstractRecipeRegistry<SmeltingRecipe> implements SmeltingRecipeRegistry {

    @Override
    public Optional<SmeltingRecipe> findMatchingRecipe(ItemStackSnapshot ingredient) {
        checkNotNull(ingredient, "ingredient");
        for (SmeltingRecipe recipe : getAll()) {
            if (recipe.isValid(ingredient)) {
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<SmeltingResult> getResult(ItemStackSnapshot ingredient) {
        checkNotNull(ingredient, "ingredient");
        for (SmeltingRecipe recipe : getAll()) {
            final Optional<SmeltingResult> result = recipe.getResult(ingredient);
            if (result.isPresent()) {
                return result;
            }
        }
        return Optional.empty();
    }

    @DelayedRegistration(RegistrationPhase.POST_INIT)
    @Override
    public void registerDefaults() {
        final CauseStack causeStack = CauseStack.current();
        try (CauseStack.Frame frame = causeStack.pushCauseFrame()) {
            frame.pushCause(Lantern.getMinecraftPlugin());

            register(ISmeltingRecipe.builder()
                    .ingredient(ItemTypes.COBBLESTONE)
                    .result(ItemStack.of(ItemTypes.STONE, 1))
                    .id("stone")
                    .build());

            final ItemStack result = ItemStack.of(ItemTypes.IRON_INGOT, 1);
            result.offer(Keys.DISPLAY_NAME, Text.of(TextColors.RESET, "Steel Ingot"));
            register(ISmeltingRecipe.builder()
                    .ingredient(IIngredient.builder().with(ItemTypes.IRON_INGOT).withQuantity(2).build(),
                            ItemStack.of(ItemTypes.IRON_INGOT, 1).createSnapshot())
                    .result(result)
                    .id("steel_ingot")
                    .build());
        }
    }
}
