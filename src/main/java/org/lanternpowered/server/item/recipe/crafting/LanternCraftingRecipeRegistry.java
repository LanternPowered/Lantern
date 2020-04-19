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
package org.lanternpowered.server.item.recipe.crafting;

import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.item.ItemTypeRegistry;
import org.lanternpowered.server.item.recipe.AbstractRecipeRegistry;
import org.lanternpowered.server.item.recipe.IIngredient;
import org.lanternpowered.server.util.ReflectionHelper;
import org.lanternpowered.server.util.UncheckedThrowables;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.crafting.CraftingGridInventory;
import org.spongepowered.api.item.recipe.crafting.CraftingRecipe;
import org.spongepowered.api.item.recipe.crafting.CraftingResult;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.registry.RegistrationPhase;
import org.spongepowered.api.registry.util.DelayedRegistration;
import org.spongepowered.api.registry.util.RegistrationDependency;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.format.TextColors;
import org.spongepowered.api.world.World;

import java.util.Optional;

@RegistrationDependency(ItemTypeRegistry.class)
public class LanternCraftingRecipeRegistry extends AbstractRecipeRegistry<CraftingRecipe> implements ICraftingRecipeRegistry {

    @Override
    public Optional<CraftingRecipe> findMatchingRecipe(CraftingGridInventory grid, World world) {
        final CraftingMatrix craftingMatrix = CraftingMatrix.of(grid);
        for (CraftingRecipe recipe : getAll()) {
            final boolean result;
            if (recipe instanceof ICraftingRecipe) {
                result = ((ICraftingRecipe) recipe).isValid(craftingMatrix, world);
            } else {
                result = recipe.isValid(grid, world);
            }
            if (result) {
                return Optional.of(recipe);
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<CraftingResult> getResult(CraftingGridInventory grid, World world) {
        final CraftingMatrix craftingMatrix = CraftingMatrix.of(grid);
        for (CraftingRecipe recipe : getAll()) {
            final Optional<CraftingResult> optResult;
            if (recipe instanceof ICraftingRecipe) {
                optResult = ((ICraftingRecipe) recipe).getResult(craftingMatrix, world);
            } else {
                optResult = recipe.getResult(grid, world);
            }
            if (optResult.isPresent()) {
                return optResult;
            }
        }
        return Optional.empty();
    }

    @Override
    public Optional<ExtendedCraftingResult> getExtendedResult(CraftingGridInventory grid, World world, int timesLimit) {
        final CraftingMatrix craftingMatrix = CraftingMatrix.of(grid);
        for (CraftingRecipe recipe : getAll()) {
            final Optional<ExtendedCraftingResult> optResult;
            if (recipe instanceof ICraftingRecipe) {
                optResult = ((ICraftingRecipe) recipe).getExtendedResult(craftingMatrix, world, timesLimit);
            } else {
                optResult = recipe.getResult(grid, world).map(result -> {
                    // Just assume that normal recipes only decrease one item per slot
                    int maxTimes = -1;
                    for (int x = 0; x < craftingMatrix.width(); x++) {
                        for (int y = 0; y < craftingMatrix.height(); y++) {
                            final ItemStack itemStack = craftingMatrix.get(x, y);
                            if (!itemStack.isEmpty()) {
                                final int times1 = itemStack.getQuantity();
                                if (maxTimes == -1 || times1 < maxTimes) {
                                    maxTimes = times1;
                                }
                            }
                        }
                    }
                    if (maxTimes > timesLimit) {
                        maxTimes = timesLimit;
                    }
                    return new ExtendedCraftingResult(result, craftingMatrix, maxTimes);
                });
            }
            if (optResult.isPresent()) {
                return optResult;
            }
        }
        return Optional.empty();
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

        final CauseStack causeStack = CauseStack.current();

        try (CauseStack.Frame frame = causeStack.pushCauseFrame()) {
            frame.pushCause(Lantern.getMinecraftPlugin());
            register(ICraftingRecipe.shapedBuilder()
                    .aisle("x", "x")
                    .where('x', Ingredient.of(ItemTypes.OAK_PLANKS))
                    .result(ItemStack.of(ItemTypes.STICK, 4))
                    .id("stick")
                    .build());
        }

        // Extra, testing stuff
        try (CauseStack.Frame frame = causeStack.pushCauseFrame()) {
            frame.pushCause(Lantern.getImplementationPlugin());

            // Euro?
            ItemStack result = ItemStack.of(ItemTypes.GOLD_NUGGET, 4);
            result.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, "€1"));
            register(ICraftingRecipe.shapedBuilder()
                    .aisle("x", "y")
                    .where('x', Ingredient.of(ItemTypes.GOLD_INGOT))
                    .where('y', IIngredient.builder().with(ItemTypes.LAVA_BUCKET).withRemaining(ItemTypes.BUCKET).build())
                    .result(result)
                    .id("one_euro")
                    .build());

            // Two sticks?
            register(ICraftingRecipe.shapelessBuilder()
                    .addIngredients(Ingredient.of(ItemTypes.STICK), 2)
                    .result(ItemStack.of(ItemTypes.STICK, 2))
                    .id("two_sticks")
                    .build());

            // Two buckets?
            result = ItemStack.of(ItemTypes.LAVA_BUCKET, 1);
            result.offer(Keys.DISPLAY_NAME, Text.of(TextColors.GOLD, "¿Compressed Lava?"));
            register(ICraftingRecipe.shapelessBuilder()
                    .addIngredient(Ingredient.of(ItemTypes.LAVA_BUCKET))
                    .addIngredient(IIngredient.builder().with(ItemTypes.LAVA_BUCKET).withRemaining(ItemTypes.BUCKET).build())
                    .result(result)
                    .id("compressed_lava")
                    .build());
        }
    }
}
