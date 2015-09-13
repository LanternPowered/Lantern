package org.lanternpowered.server.data.value.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Optional;

import org.lanternpowered.server.data.value.mutable.LanternOptionalValue;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableOptionalValue;
import org.spongepowered.api.data.value.immutable.ImmutableValue;
import org.spongepowered.api.data.value.mutable.OptionalValue;

import javax.annotation.Nullable;

public class ImmutableLanternOptionalValue<E> extends ImmutableLanternValue<Optional<E>> implements ImmutableOptionalValue<E> {

    public ImmutableLanternOptionalValue(Key<? extends BaseValue<Optional<E>>> key) {
        super(key, Optional.<E>absent());
    }

    public ImmutableLanternOptionalValue(Key<? extends BaseValue<Optional<E>>> key, Optional<E> actualValue) {
        super(key, Optional.<E>absent(), actualValue);
    }

    @Override
    public ImmutableOptionalValue<E> with(Optional<E> value) {
        return new ImmutableLanternOptionalValue<E>(getKey(), checkNotNull(value));
    }

    @Override
    public ImmutableOptionalValue<E> transform(Function<Optional<E>, Optional<E>> function) {
        return new ImmutableLanternOptionalValue<E>(getKey(), checkNotNull(function.apply(get())));
    }

    @Override
    public OptionalValue<E> asMutable() {
        return new LanternOptionalValue<E>(getKey(), this.actualValue);
    }

    @Override
    public ImmutableOptionalValue<E> instead(@Nullable E value) {
        return new ImmutableLanternOptionalValue<E>(getKey(), Optional.fromNullable(value));
    }

    @Override
    public ImmutableValue<E> or(E value) { // TODO actually construct a new key for this kind...
        return new ImmutableLanternValue<E>(null, get().isPresent() ? get().get() : checkNotNull(value));
    }
}
