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
    private final List<ItemStackSnapshot> displayedItems;
    @Nullable private final Function<ItemStack, ItemStack> remainingItemProvider;

    LanternIngredient(Predicate<ItemStack> matcher, List<ItemStackSnapshot> displayedItems,
            @Nullable Function<ItemStack, ItemStack> remainingItemProvider) {
        this.matcher = matcher;
        this.displayedItems = displayedItems;
        this.remainingItemProvider = remainingItemProvider;
    }

    @Override
    public Optional<ItemStack> getRemainingItem(ItemStack itemStack) {
        return this.remainingItemProvider == null ? Optional.empty() :
                Optional.ofNullable(this.remainingItemProvider.apply(itemStack));
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
