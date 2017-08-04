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
package org.lanternpowered.server.item.recipe;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nullable;

final class LanternIngredient implements IIngredient {

    final Predicate<ItemStack> matcher;
    final IIngredientQuantityProvider quantityProvider;
    private final List<ItemStackSnapshot> displayedItems;
    @Nullable final Function<ItemStack, ItemStack> remainingItemProvider;

    LanternIngredient(Predicate<ItemStack> matcher, IIngredientQuantityProvider quantityProvider,
            List<ItemStackSnapshot> displayedItems, @Nullable Function<ItemStack, ItemStack> remainingItemProvider) {
        this.matcher = matcher;
        this.quantityProvider = quantityProvider;
        this.displayedItems = displayedItems;
        this.remainingItemProvider = remainingItemProvider;
    }

    @Override
    public Optional<ItemStack> getRemainingItem(ItemStack itemStack) {
        return this.remainingItemProvider == null ? Optional.empty() :
                Optional.ofNullable(this.remainingItemProvider.apply(itemStack));
    }

    @Override
    public int getQuantity(ItemStackSnapshot itemStackSnapshot) {
        return this.quantityProvider.get(itemStackSnapshot);
    }

    @Override
    public int getQuantity(ItemStack itemStack) {
        return this.quantityProvider.get(itemStack);
    }

    @Override
    public boolean test(ItemStack itemStack) {
        return this.matcher.test(itemStack);
    }

    @Override
    public List<ItemStackSnapshot> displayedItems() {
        return this.displayedItems;
    }
}
