package org.lanternpowered.server.item.recipe;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.inventory.LanternItemStack;
import org.lanternpowered.server.util.functions.Predicates;
import org.spongepowered.api.GameDictionary;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.item.recipe.crafting.Ingredient;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nullable;

public class LanternIngredientBuilder implements IIngredient.Builder {

    private List<Predicate<ItemStack>> matchers = new ArrayList<>();
    private List<ItemStackSnapshot> displayItems = new ArrayList<>();
    @Nullable private Function<ItemStack, ItemStack> remainingItemProvider;

    @Override
    public Ingredient.Builder from(Ingredient value) {
        reset();
        this.matchers.add(((LanternIngredient) value).matcher);
        this.displayItems.addAll(value.displayedItems());
        return this;
    }

    @Override
    public Ingredient.Builder reset() {
        this.matchers.clear();
        this.displayItems.clear();
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
    public IIngredient.Builder with(GameDictionary.Entry entry) {
        checkNotNull(entry, "entry");
        this.matchers.add(entry::matches);
        return this;
    }

    @Override
    public IIngredient.Builder with(ItemStackSnapshot... items) {
        checkNotNull(items, "items");
        for (ItemStackSnapshot item : items) {
            checkNotNull(item, "item");
            with(item.createStack());
        }
        return withDisplay(items);
    }

    @Override
    public IIngredient.Builder with(ItemStack... items) {
        checkNotNull(items, "items");
        for (ItemStack item : items) {
            checkNotNull(item, "item");
            this.matchers.add(itemStack -> LanternItemStack.isSimilar(itemStack, item));
        }
        return withDisplay(items);
    }

    @Override
    public IIngredient.Builder with(ItemType... types) {
        checkNotNull(types, "types");
        for (ItemType type : types) {
            checkNotNull(type, "type");
            this.matchers.add(type::matches);
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
        return new LanternIngredient(Predicates.or(this.matchers), this.displayItems, this.remainingItemProvider);
    }
}
