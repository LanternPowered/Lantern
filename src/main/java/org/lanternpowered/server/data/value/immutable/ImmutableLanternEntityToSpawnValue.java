package org.lanternpowered.server.data.value.immutable;

import static com.google.common.base.Preconditions.checkNotNull;

import com.google.common.base.Function;

import org.lanternpowered.server.data.value.mutable.LanternNextEntityToSpawnValue;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.immutable.ImmutableMobSpawnerData;
import org.spongepowered.api.data.manipulator.mutable.MobSpawnerData;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.util.weighted.WeightedEntity;

import java.util.Collection;

import javax.annotation.Nullable;

public class ImmutableLanternEntityToSpawnValue extends ImmutableLanternValue<WeightedEntity>
        implements ImmutableMobSpawnerData.ImmutableNextEntityToSpawnValue {

    public ImmutableLanternEntityToSpawnValue(WeightedEntity actualValue) {
        super(Keys.SPAWNER_NEXT_ENTITY_TO_SPAWN, new WeightedEntity(EntityTypes.CREEPER, 1), actualValue);
    }

    @Override
    public ImmutableLanternEntityToSpawnValue with(WeightedEntity value) {
        return new ImmutableLanternEntityToSpawnValue(checkNotNull(value));
    }

    @Override
    public ImmutableLanternEntityToSpawnValue transform(Function<WeightedEntity, WeightedEntity> function) {
        final WeightedEntity value = checkNotNull(function).apply(get());
        return new ImmutableLanternEntityToSpawnValue(checkNotNull(value));
    }

    @Override
    public MobSpawnerData.NextEntityToSpawnValue asMutable() {
        return new LanternNextEntityToSpawnValue(this.actualValue);
    }

    @Override
    public ImmutableMobSpawnerData.ImmutableNextEntityToSpawnValue with(EntityType type,
            @Nullable Collection<DataManipulator<?, ?>> additionalProperties) {
        if (additionalProperties == null) {
            return new ImmutableLanternEntityToSpawnValue(new WeightedEntity(checkNotNull(type), 1));
        } else {
            return new ImmutableLanternEntityToSpawnValue(new WeightedEntity(checkNotNull(type), 1, additionalProperties));
        }
    }
}
