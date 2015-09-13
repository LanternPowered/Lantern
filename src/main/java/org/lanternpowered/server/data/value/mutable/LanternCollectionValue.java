package org.lanternpowered.server.data.value.mutable;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Optional;
import com.google.common.base.Predicate;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableCollectionValue;
import org.spongepowered.api.data.value.mutable.CollectionValue;

import java.util.Collection;
import java.util.Iterator;

@SuppressWarnings("unchecked")
public abstract class LanternCollectionValue<E, V extends Collection<E>, I extends CollectionValue<E, V, I, L>,
    L extends ImmutableCollectionValue<E, V, L, I>> extends LanternValue<V> implements CollectionValue<E, V, I, L> {


    public LanternCollectionValue(Key<? extends BaseValue<V>> key, V defaultValue) {
        super(key, defaultValue);
    }

    public LanternCollectionValue(Key<? extends BaseValue<V>> key, V defaultValue, V actualValue) {
        super(key, defaultValue, actualValue);
    }

    @Override
    public I set(V value) {
        this.actualValue = checkNotNull(value);
        return (I) this;
    }

    @Override
    public I transform(Function<V, V> function) {
        this.actualValue = checkNotNull(function).apply(this.actualValue);
        return (I) this;
    }

    @Override
    public int size() {
        return this.actualValue.size();
    }

    @Override
    public boolean isEmpty() {
        return this.actualValue.isEmpty();
    }

    @Override
    public I add(E element) {
        this.actualValue.add(checkNotNull(element));
        return (I) this;
    }

    @Override
    public I addAll(Iterable<E> elements) {
        for (E element : checkNotNull(elements)) {
            this.actualValue.add(checkNotNull(element));
        }
        return (I) this;
    }

    @Override
    public I remove(E element) {
        this.actualValue.remove(checkNotNull(element));
        return (I) this;
    }

    @Override
    public I removeAll(Iterable<E> elements) {
        for (E element : elements) {
            this.actualValue.remove(checkNotNull(element));
        }
        return (I) this;
    }

    @Override
    public I removeAll(Predicate<E> predicate) {
        for (Iterator<E> iterator = this.actualValue.iterator(); iterator.hasNext(); ) {
            if (checkNotNull(predicate).apply(iterator.next())) {
                iterator.remove();
            }
        }
        return (I) this;
    }

    @Override
    public boolean contains(E element) {
        return this.actualValue.contains(checkNotNull(element));
    }

    @Override
    public boolean containsAll(Collection<E> iterable) {
        return this.actualValue.containsAll(iterable);
    }

    @Override
    public boolean exists() {
        return this.actualValue != null;
    }

    @Override
    public abstract L asImmutable();

    @Override
    public Optional<V> getDirect() {
        return Optional.of(this.actualValue);
    }

    @Override
    public Iterator<E> iterator() {
        return this.actualValue.iterator();
    }
}
