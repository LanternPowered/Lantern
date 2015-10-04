package org.lanternpowered.server.data.value.immutable;

import com.google.common.collect.Iterables;

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableCollectionValue;
import org.spongepowered.api.data.value.mutable.CollectionValue;

import java.util.Collection;
import java.util.function.Function;

public abstract class ImmutableLanternCollectionValue<E, V extends Collection<E>, I extends ImmutableCollectionValue<E, V, I, L>,
    L extends CollectionValue<E, V, L, I>> extends ImmutableLanternValue<V> implements ImmutableCollectionValue<E, V, I, L> {

    protected ImmutableLanternCollectionValue(Key<? extends BaseValue<V>> key, V defaultValue) {
        super(key, defaultValue);
    }

    protected ImmutableLanternCollectionValue(Key<? extends BaseValue<V>> key, V defaultValue, V actualValue) {
        super(key, defaultValue, actualValue);
    }

    @Override
    public abstract I with(V value);

    @Override
    public abstract I transform(Function<V, V> function);

    @Override
    public abstract L asMutable();

    @Override
    public int size() {
        return this.actualValue.size();
    }

    @Override
    public boolean isEmpty() {
        return this.actualValue.isEmpty();
    }


    @Override
    public boolean contains(E element) {
        return this.actualValue.contains(element);
    }

    @Override
    public boolean containsAll(Iterable<E> iterable) {
        for (E element : iterable) {
            if (!Iterables.contains(this.actualValue, element)) {
                return false;
            }
        }
        return true;
    }
}
