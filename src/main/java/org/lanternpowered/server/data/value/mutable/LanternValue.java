package org.lanternpowered.server.data.value.mutable;

import com.google.common.base.Function;

import org.lanternpowered.server.data.value.AbstractBaseValue;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;

public class LanternValue<E> extends AbstractBaseValue<E> implements Value<E> {

    public LanternValue(Key<? extends BaseValue<E>> key, E defaultValue) {
        this(key, defaultValue, defaultValue);
    }

    public LanternValue(Key<? extends BaseValue<E>> key, E defaultValue, E actualValue) {
        super(key, defaultValue, actualValue);
    }

    @Override
    public Value<E> set(E value) {
        this.actualValue = value;
        return this;
    }

    @Override
    public Value<E> transform(Function<E, E> function) {
        this.actualValue = function.apply(this.actualValue);
        return this;
    }

    @Override
    public ImmutableValue<E> asImmutable() {
        // TODO
        return null;
        // return ImmutableDataCachingUtil.getValue(ImmutableSpongeValue.class, this.getKey(), this.actualValue, this.getDefault());
    }
}
