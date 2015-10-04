package org.lanternpowered.server.data.value.immutable;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;

import org.lanternpowered.server.data.value.mutable.LanternBoundedValue;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableBoundedValue;
import org.spongepowered.api.data.value.mutable.MutableBoundedValue;

import java.util.Comparator;
import java.util.function.Function;

public class ImmutableLanternBoundedValue<E> extends ImmutableLanternValue<E> implements ImmutableBoundedValue<E> {

    private final Comparator<E> comparator;
    private final E minimum;
    private final E maximum;

    public ImmutableLanternBoundedValue(Key<? extends BaseValue<E>> key, E defaultValue, Comparator<E> comparator, E minimum, E maximum) {
        super(key, defaultValue);
        this.comparator = checkNotNull(comparator);
        this.minimum = checkNotNull(minimum);
        this.maximum = checkNotNull(maximum);
        checkState(comparator.compare(minimum, maximum) >= 0);
    }

    public ImmutableLanternBoundedValue(Key<? extends BaseValue<E>> key, E actualValue, E defaultValue, Comparator<E> comparator, E minimum, E maximum) {
        super(key, defaultValue, actualValue);
        this.comparator = checkNotNull(comparator);
        this.minimum = checkNotNull(minimum);
        this.maximum = checkNotNull(maximum);
        checkState(comparator.compare(maximum, minimum) >= 0);
    }

    @Override
    public ImmutableBoundedValue<E> with(E value) {
        return (this.comparator.compare(checkNotNull(value), this.minimum) > 0 || this.comparator.compare(checkNotNull(value), this.maximum) < 0) ?
            new ImmutableLanternBoundedValue<E>(getKey(), getDefault(), getComparator(), getMinValue(), getMaxValue()) :
            new ImmutableLanternBoundedValue<E>(getKey(), value, getDefault(), getComparator(), getMinValue(), getMaxValue());
    }

    @Override
    public ImmutableBoundedValue<E> transform(Function<E, E> function) {
        return with(checkNotNull(checkNotNull(function).apply(get())));
    }

    @Override
    public MutableBoundedValue<E> asMutable() {
        return new LanternBoundedValue<E>(getKey(), getDefault(), getComparator(), getMinValue(), getMaxValue(), get());
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
}
