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
package org.lanternpowered.server.item.recipe.fuel;

import org.spongepowered.api.CatalogType;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;
import org.spongepowered.api.util.ResettableBuilder;

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
    Ingredient getIngredient();

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

    interface Builder extends ResettableBuilder<IFuel, Builder> {

        Builder ingredient(Ingredient ingredient);

        Builder ingredient(ItemType itemType);

        Builder ingredient(ItemStack itemStack);

        Builder ingredient(ItemStackSnapshot itemStackSnapshot);

        Builder burnTime(IFuelBurnTimeProvider provider);

        Builder burnTime(int ticks);

        IFuel build(String id, Object plugin);
    }
}
