package org.lanternpowered.server.data.value.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;

import org.lanternpowered.server.data.value.AbstractBaseValue;
import org.lanternpowered.server.data.value.mutable.LanternValue;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.Value;

public class ImmutableLanternValue<E> extends AbstractBaseValue<E> implements ImmutableValue<E> {

    public ImmutableLanternValue(Key<? extends BaseValue<E>> key, E defaultValue) {
        super(key, defaultValue, defaultValue);
    }

    public ImmutableLanternValue(Key<? extends BaseValue<E>> key, E defaultValue, E actualValue) {
        super(key, defaultValue, actualValue);
    }

    @Override
    public ImmutableValue<E> with(E value) {
        return new ImmutableLanternValue<E>(this.getKey(), getDefault(), value);
    }

    @Override
    public ImmutableValue<E> transform(Function<E, E> function) {
        final E value = checkNotNull(function).apply(get());
        return new ImmutableLanternValue<E>(this.getKey(), getDefault(), value);
    }

    @Override
    public Value<E> asMutable() {
        return new LanternValue<E>(getKey(), getDefault(), get());
    }
}
