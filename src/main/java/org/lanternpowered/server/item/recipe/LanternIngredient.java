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
package org.lanternpowered.server.item.recipe;

import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;

import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.Nullable;

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
