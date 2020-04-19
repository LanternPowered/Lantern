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

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.util.function.Predicates;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import org.checkerframework.checker.nullness.qual.Nullable;

public class LanternIngredientBuilder implements IIngredient.Builder {

    private static final IIngredientQuantityProvider DEFAULT_QUANTITY_PROVIDER = new ConstantIngredientQuantityProvider(1);

    private List<Predicate<ItemStack>> matchers = new ArrayList<>();
    private List<ItemStackSnapshot> displayItems = new ArrayList<>();
    @Nullable private IIngredientQuantityProvider quantityProvider;
    @Nullable private Function<ItemStack, ItemStack> remainingItemProvider;

    @Override
    public Ingredient.Builder from(Ingredient value) {
        this.matchers.clear();
        this.matchers.add(((LanternIngredient) value).matcher);
        this.displayItems.addAll(value.displayedItems());
        this.remainingItemProvider = ((LanternIngredient) value).remainingItemProvider;
        this.quantityProvider = ((LanternIngredient) value).quantityProvider;
        return this;
    }

    @Override
    public Ingredient.Builder reset() {
        this.matchers.clear();
        this.displayItems.clear();
        this.remainingItemProvider = null;
        this.quantityProvider = null;
        return this;
    }

    @Override
    public IIngredient.Builder withQuantity(int quantity) {
        checkArgument(quantity > 1, "The quantity must be greater then 1");
        this.quantityProvider = new ConstantIngredientQuantityProvider(quantity);
        return this;
    }

    @Override
    public IIngredient.Builder withQuantity(IIngredientQuantityProvider quantityProvider) {
        checkNotNull(quantityProvider, "quantityProvider");
        this.quantityProvider = quantityProvider;
        return this;
    }

    @Override
    public IIngredient.Builder withRemaining(ItemType type) {
        checkNotNull(type, "type");
        this.remainingItemProvider = itemStack -> ItemStack.of(type, 1);
        return this;
    }

    @Override
    public IIngredient.Builder withRemaining(ItemStack item) {
        checkNotNull(item, "item");
        this.remainingItemProvider = itemStack -> item.copy();
        return this;
    }

    @Override
    public IIngredient.Builder withRemaining(ItemStackSnapshot item) {
        checkNotNull(item, "item");
        this.remainingItemProvider = itemStack -> item.createStack();
        return this;
    }

    @Override
    public IIngredient.Builder withRemaining(Function<ItemStack, ItemStack> provider) {
        checkNotNull(provider, "provider");
        this.remainingItemProvider = provider;
        return this;
    }

    @Override
    public IIngredient.Builder with(Predicate<ItemStack> predicate) {
        checkNotNull(predicate, "predicate");
        this.matchers.add(predicate);
        return this;
    }

    @Override
    public IIngredient.Builder with(ItemStackSnapshot... items) {
        checkNotNull(items, "items");
        for (ItemStackSnapshot item : items) {
            checkNotNull(item, "item");
            final ItemStack item1 = item.createStack();
            this.matchers.add(itemStack -> LanternItemStack.areSimilar(itemStack, item1));
        }
        return withDisplay(items);
    }

    @Override
    public IIngredient.Builder with(ItemStack... items) {
        checkNotNull(items, "items");
        for (ItemStack item : items) {
            checkNotNull(item, "item");
            final ItemStack item1 = item.copy(); // Create a copy to be safe
            this.matchers.add(itemStack -> LanternItemStack.areSimilar(itemStack, item1));
        }
        return withDisplay(items);
    }

    @Override
    public IIngredient.Builder with(ItemType... types) {
        checkNotNull(types, "types");
        for (ItemType type : types) {
            checkNotNull(type, "type");
            this.matchers.add(itemStack -> itemStack.getType().equals(type));
        }
        return withDisplay(types);
    }

    @Override
    public IIngredient.Builder withDisplay(ItemType... types) {
        checkNotNull(types, "types");
        for (ItemType type : types) {
            checkNotNull(type, "type");
            this.displayItems.add(ItemStack.of(type, 1).createSnapshot());
        }
        return this;
    }

    @Override
    public IIngredient.Builder withDisplay(ItemStack... items) {
        checkNotNull(items, "items");
        for (ItemStack item : items) {
            checkNotNull(item, "item");
            this.displayItems.add(item.createSnapshot());
        }
        return this;
    }

    @Override
    public IIngredient.Builder withDisplay(ItemStackSnapshot... items) {
        checkNotNull(items, "items");
        for (ItemStackSnapshot item : items) {
            checkNotNull(item, "item");
            this.displayItems.add(item);
        }
        return this;
    }

    @Override
    public IIngredient build() {
        checkState(!this.matchers.isEmpty(), "At least one matcher must be added");
        checkState(!this.displayItems.isEmpty(), "At least one displayItem must be added");
        IIngredientQuantityProvider quantityProvider = this.quantityProvider;
        if (quantityProvider == null) {
            quantityProvider = DEFAULT_QUANTITY_PROVIDER;
        }
        return new LanternIngredient(Predicates.or(this.matchers), quantityProvider,
                new ArrayList<>(this.displayItems), this.remainingItemProvider);
    }
}
