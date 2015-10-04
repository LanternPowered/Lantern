package org.lanternpowered.server.data.value.mutable;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Optional;
import java.util.function.Function;

import org.lanternpowered.server.data.value.immutable.ImmutableLanternOptionalValue;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableOptionalValue;
import org.spongepowered.api.data.value.mutable.OptionalValue;
import org.spongepowered.api.data.value.mutable.Value;

import javax.annotation.Nullable;

public class LanternOptionalValue<E> extends LanternValue<Optional<E>> implements OptionalValue<E> {

    public LanternOptionalValue(Key<? extends BaseValue<Optional<E>>> key) {
        this(key, Optional.<E>empty());
    }

    public LanternOptionalValue(Key<? extends BaseValue<Optional<E>>> key, Optional<E> actualValue) {
        this(key, Optional.<E>empty(), actualValue);
    }

    public LanternOptionalValue(Key<? extends BaseValue<Optional<E>>> key, Optional<E> defaultValue, Optional<E> actualValue) {
        super(key, defaultValue, actualValue);
    }

    @Override
    public OptionalValue<E> set(Optional<E> value) {
        this.actualValue = checkNotNull(value);
        return this;
    }

    @Override
    public OptionalValue<E> transform(Function<Optional<E>, Optional<E>> function) {
        this.actualValue = checkNotNull(function.apply(this.actualValue));
        return this;
    }

    @Override
    public ImmutableOptionalValue<E> asImmutable() {
        return new ImmutableLanternOptionalValue<E>(getKey(), this.actualValue);
    }

    @Override
    public OptionalValue<E> setTo(@Nullable E value) {
        return set(Optional.ofNullable(value));
    }

    @Override
    public Value<E> or(E defaultValue) { // TODO actually construct the keys
        return new LanternValue<E>(null, null, get().isPresent() ? get().get() : checkNotNull(defaultValue));
    }
}
