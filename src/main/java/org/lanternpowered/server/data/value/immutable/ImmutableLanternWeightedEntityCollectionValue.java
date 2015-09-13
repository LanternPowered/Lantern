package org.lanternpowered.server.data.value.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;
import com.google.common.base.Predicate;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;

import org.lanternpowered.server.data.value.mutable.LanternWeightedEntityCollectionValue;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableWeightedEntityCollectionValue;
import org.spongepowered.api.data.value.mutable.WeightedEntityCollectionValue;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.util.weighted.WeightedCollection;
import org.spongepowered.api.util.weighted.WeightedEntity;

import java.util.Collection;
import java.util.Random;

import javax.annotation.Nullable;

public class ImmutableLanternWeightedEntityCollectionValue extends ImmutableLanternWeightedCollectionValue<WeightedEntity,
    ImmutableWeightedEntityCollectionValue, WeightedEntityCollectionValue> implements ImmutableWeightedEntityCollectionValue {

    public ImmutableLanternWeightedEntityCollectionValue(Key<? extends BaseValue<WeightedCollection<WeightedEntity>>> key) {
        super(key);
    }

    public ImmutableLanternWeightedEntityCollectionValue(Key<? extends BaseValue<WeightedCollection<WeightedEntity>>> key,
                                                        WeightedCollection<WeightedEntity> actualValue) {
        super(key, actualValue);
    }

    @Override
    public ImmutableWeightedEntityCollectionValue with(WeightedCollection<WeightedEntity> value) {
        final WeightedCollection<WeightedEntity> weightedEntities = new WeightedCollection<WeightedEntity>();
        weightedEntities.addAll(this.actualValue);
        weightedEntities.addAll(checkNotNull(value));
        return new ImmutableLanternWeightedEntityCollectionValue(getKey(), weightedEntities);
    }

    @Override
    public ImmutableWeightedEntityCollectionValue transform(
        Function<WeightedCollection<WeightedEntity>, WeightedCollection<WeightedEntity>> function) {
        final WeightedCollection<WeightedEntity> weightedEntities = new WeightedCollection<WeightedEntity>();
        final WeightedCollection<WeightedEntity> temp = new WeightedCollection<WeightedEntity>();
        temp.addAll(this.actualValue);
        weightedEntities.addAll(checkNotNull(checkNotNull(function).apply(temp)));
        return new ImmutableLanternWeightedEntityCollectionValue(getKey(), weightedEntities);
    }

    @Override
    public ImmutableWeightedEntityCollectionValue with(WeightedEntity... elements) {
        final WeightedCollection<WeightedEntity> weightedEntities = new WeightedCollection<WeightedEntity>();
        weightedEntities.addAll(this.actualValue);
        weightedEntities.addAll(ImmutableList.copyOf(elements));
        return new ImmutableLanternWeightedEntityCollectionValue(getKey(), weightedEntities);
    }

    @Override
    public ImmutableWeightedEntityCollectionValue withAll(Iterable<WeightedEntity> elements) {
        final WeightedCollection<WeightedEntity> weightedEntities = new WeightedCollection<WeightedEntity>();
        weightedEntities.addAll(this.actualValue);
        Iterables.addAll(weightedEntities, elements);
        return new ImmutableLanternWeightedEntityCollectionValue(getKey(), weightedEntities);
    }

    @Override
    public ImmutableWeightedEntityCollectionValue without(WeightedEntity element) {
        final WeightedCollection<WeightedEntity> weightedEntities = new WeightedCollection<WeightedEntity>();
        for (WeightedEntity entity : this.actualValue) {
            if (!entity.equals(element)) {
                weightedEntities.add(entity);
            }
        }
        return new ImmutableLanternWeightedEntityCollectionValue(getKey(), weightedEntities);
    }

    @Override
    public ImmutableWeightedEntityCollectionValue withoutAll(Iterable<WeightedEntity> elements) {
        final WeightedCollection<WeightedEntity> weightedEntities = new WeightedCollection<WeightedEntity>();
        for (WeightedEntity entity : this.actualValue) {
            if (!Iterables.contains(elements, entity)) {
                weightedEntities.add(entity);
            }
        }
        return new ImmutableLanternWeightedEntityCollectionValue(getKey(), weightedEntities);
    }

    @Override
    public ImmutableWeightedEntityCollectionValue withoutAll(Predicate<WeightedEntity> predicate) {
        final WeightedCollection<WeightedEntity> weightedEntities = new WeightedCollection<WeightedEntity>();
        for (WeightedEntity entity : this.actualValue) {
            if (!predicate.apply(entity)) {
                weightedEntities.add(entity);
            }
        }
        return new ImmutableLanternWeightedEntityCollectionValue(getKey(), weightedEntities);
    }

    @Override
    public WeightedEntityCollectionValue asMutable() {
        return new LanternWeightedEntityCollectionValue(getKey(), getAll());
    }

    @Override
    public ImmutableWeightedEntityCollectionValue with(EntityType entityType, Collection<DataManipulator<?, ?>> entityData) {
        return with(new WeightedEntity(entityType, 1, entityData));
    }

    @Nullable
    @Override
    public WeightedEntity get(Random random) {
        return this.actualValue.get(checkNotNull(random));
    }
}
