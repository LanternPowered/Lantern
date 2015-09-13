package org.lanternpowered.server.data.value.immutable;

import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableWeightedCollectionValue;
import org.spongepowered.api.data.value.mutable.WeightedCollectionValue;
import org.spongepowered.api.util.weighted.WeightedCollection;
import org.spongepowered.api.util.weighted.WeightedObject;

public abstract class ImmutableLanternWeightedCollectionValue<E extends WeightedObject<?>, I extends ImmutableWeightedCollectionValue<E, I, M>,
    M extends WeightedCollectionValue<E, M, I>> extends ImmutableLanternCollectionValue<E, WeightedCollection<E>, I, M> implements
ImmutableWeightedCollectionValue<E, I, M> {

    public ImmutableLanternWeightedCollectionValue(Key<? extends BaseValue<WeightedCollection<E>>> key) {
        super(key, new WeightedCollection<E>());
    }

    public ImmutableLanternWeightedCollectionValue(Key<? extends BaseValue<WeightedCollection<E>>> key, WeightedCollection<E> actualValue) {
        super(key, new WeightedCollection<E>(), actualValue);
    }

    @Override
    public WeightedCollection<E> getAll() {
        final WeightedCollection<E> weightedCollection = new WeightedCollection<E>();
        weightedCollection.addAll(this.actualValue);
        return weightedCollection;
    }
}
