package org.lanternpowered.server.data.value.mutable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Function;

import org.lanternpowered.server.data.value.immutable.ImmutableLanternItemValue;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.item.inventory.ItemStack;

public class LanternItemValue extends LanternValue<ItemStack> {

    public LanternItemValue(Key<? extends BaseValue<ItemStack>> key, ItemStack defaultValue) {
        super(key, defaultValue.copy());
    }

    public LanternItemValue(Key<? extends BaseValue<ItemStack>> key, ItemStack defaultValue, ItemStack actualValue) {
        super(key, defaultValue.copy(), actualValue.copy());
    }

    @Override
    public ItemStack get() {
        return super.get().copy();
    }

    @Override
    public Value<ItemStack> set(ItemStack value) {
        return super.set(value.copy());
    }

    @Override
    public Value<ItemStack> transform(Function<ItemStack, ItemStack> function) {
        this.actualValue = checkNotNull(checkNotNull(function).apply(this.actualValue)).copy();
        return this;
    }

    @Override
    public ImmutableValue<ItemStack> asImmutable() {
        return new ImmutableLanternItemValue(this.getKey(), getDefault(), get());
    }
}
