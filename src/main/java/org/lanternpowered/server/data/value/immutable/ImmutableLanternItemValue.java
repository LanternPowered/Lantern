package org.lanternpowered.server.data.value.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.function.Function;

import org.lanternpowered.server.data.value.mutable.LanternItemValue;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.item.inventory.ItemStack;

public class ImmutableLanternItemValue extends ImmutableLanternValue<ItemStack> {

    public ImmutableLanternItemValue(Key<? extends BaseValue<ItemStack>> key, ItemStack defaultValue) {
        super(key, defaultValue.copy());
    }

    public ImmutableLanternItemValue(Key<? extends BaseValue<ItemStack>> key, ItemStack defaultValue, ItemStack actualValue) {
        super(key, defaultValue.copy(), actualValue.copy());
    }

    @Override
    public ImmutableValue<ItemStack> with(ItemStack value) {
        return super.with(value.copy());
    }

    @Override
    public ImmutableValue<ItemStack> transform(Function<ItemStack, ItemStack> function) {
        final ItemStack value = checkNotNull(function).apply(get());
        return new ImmutableLanternItemValue(this.getKey(), getDefault(), checkNotNull(value));
    }

    @Override
    public Value<ItemStack> asMutable() {
        return new LanternItemValue(getKey(), getDefault(), get());
    }

    @Override
    public ItemStack get() {
        return super.get().copy();
    }

    @Override
    public ItemStack getDefault() {
        return super.getDefault().copy();
    }
}
