package org.lanternpowered.server.data.value.mutable;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.data.value.immutable.ImmutableLanternWeightedEntityCollectionValue;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.value.BaseValue;
import org.spongepowered.api.data.value.immutable.ImmutableWeightedEntityCollectionValue;
import org.spongepowered.api.data.value.mutable.WeightedEntityCollectionValue;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.util.weighted.WeightedCollection;
import org.spongepowered.api.util.weighted.WeightedEntity;

import java.util.Collection;
import java.util.function.Predicate;

public class LanternWeightedEntityCollectionValue extends LanternWeightedCollectionValue<WeightedEntity, WeightedEntityCollectionValue,
    ImmutableWeightedEntityCollectionValue> implements WeightedEntityCollectionValue {

    public LanternWeightedEntityCollectionValue(Key<? extends BaseValue<WeightedCollection<WeightedEntity>>> key) {
        super(key);
    }

    public LanternWeightedEntityCollectionValue(Key<? extends BaseValue<WeightedCollection<WeightedEntity>>> key,
            WeightedCollection<WeightedEntity> actualValue) {
        super(key, actualValue);
    }

    @Override
    public WeightedEntityCollectionValue filter(Predicate<? super WeightedEntity> predicate) {
        final WeightedCollection<WeightedEntity> collection = new WeightedCollection<WeightedEntity>();
        for (WeightedEntity entity : this.actualValue) {
            if (checkNotNull(predicate).test(entity)) {
                collection.add(entity);
            }
        }
        return new LanternWeightedEntityCollectionValue(getKey(), collection);
    }

    @Override
    public WeightedCollection<WeightedEntity> getAll() {
        final WeightedCollection<WeightedEntity> collection = new WeightedCollection<WeightedEntity>();
        for (WeightedEntity entity : this.actualValue) {
            collection.add(entity);
        }
        return collection;
    }

    @Override
    public ImmutableWeightedEntityCollectionValue asImmutable() {
        return new ImmutableLanternWeightedEntityCollectionValue(getKey(), this.actualValue);
    }

    @Override
    public WeightedEntityCollectionValue add(EntityType entityType, Collection<DataManipulator<?, ?>> entityData) {
        return add(new WeightedEntity(checkNotNull(entityType), 1, checkNotNull(entityData)));
    }
}
