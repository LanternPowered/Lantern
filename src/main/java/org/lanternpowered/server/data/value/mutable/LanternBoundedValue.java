package org.lanternpowered.server.data.value.mutable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.data.value.immutable.ImmutableLanternBoundedValue;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableBoundedValue;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;

import java.util.Comparator;
import java.util.function.Function;

public class LanternBoundedValue<E> extends LanternValue<E> implements MutableBoundedValue<E> {

    private final Comparator<E> comparator;
    private final E minimum;
    private final E maximum;

    public LanternBoundedValue(Key<? extends BaseValue<E>> key, E defaultValue, Comparator<E> comparator, E minimum, E maximum) {
        this(key, defaultValue, comparator, minimum, maximum, defaultValue);
    }

    public LanternBoundedValue(Key<? extends BaseValue<E>> key, E defaultValue, Comparator<E> comparator, E minimum, E maximum, E actualValue) {
        super(key, defaultValue, actualValue);
        this.comparator = checkNotNull(comparator);
        this.minimum = checkNotNull(minimum);
        this.maximum = checkNotNull(maximum);
        checkState(comparator.compare(maximum, minimum) >= 0);
    }

    @Override
    public E getMinValue() {
        return this.minimum;
    }

    @Override
    public E getMaxValue() {
        return this.maximum;
    }

    @Override
    public Comparator<E> getComparator() {
        return this.comparator;
    }

    @Override
    public MutableBoundedValue<E> set(E value) {
        if (this.comparator.compare(value, this.minimum) >= 0 && this.comparator.compare(value, this.maximum) <= 0) {
            this.actualValue = checkNotNull(value);
        }
        return this;
    }

    @Override
    public MutableBoundedValue<E> transform(Function<E, E> function) {
        return set(checkNotNull(checkNotNull(function).apply(get())));
    }

    @Override
    public ImmutableBoundedValue<E> asImmutable() {
        return new ImmutableLanternBoundedValue<E>(getKey(), this.actualValue, getDefault(), this.comparator, this.minimum, this.maximum);
    }
}
