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
package org.lanternpowered.server.item.recipe.fuel;

import org.lanternpowered.server.item.recipe.IIngredient;
import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.util.CopyableBuilder;

import java.util.OptionalInt;

public interface IFuel extends CatalogType {

    static Builder builder() {
        return Sponge.getRegistry().createBuilder(Builder.class);
    }

    /**
     * Gets the {@link Ingredient} that lies at the
     * base of this fuel.
     *
     * @return The ingredient
     */
    IIngredient getIngredient();

    /**
     * Gets the amount of ticks that this fuel
     * will burn for the given {@link ItemStackSnapshot}.
     *
     * @return The burn time
     */
    default OptionalInt getBurnTime(ItemStackSnapshot input) {
        return getBurnTime(input.createStack());
    }

    /**
     * Gets the amount of ticks that this fuel
     * will burn for the given {@link ItemStack}.
     *
     * @return The burn time
     */
    OptionalInt getBurnTime(ItemStack input);

    default boolean isValid(ItemStack input) {
        return getIngredient().test(input);
    }

    default boolean isValid(ItemStackSnapshot input) {
        return isValid(input.createStack());
    }

    interface Builder extends CopyableBuilder<IFuel, Builder> {

        Builder ingredient(Ingredient ingredient);

        Builder ingredient(ItemType itemType);

        Builder ingredient(ItemStack itemStack);

        Builder ingredient(ItemStackSnapshot itemStackSnapshot);

        Builder burnTime(IFuelBurnTimeProvider provider);

        Builder burnTime(int ticks);

        IFuel build(String id, Object plugin);
    }
}
