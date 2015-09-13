package org.lanternpowered.server.data.value.mutable;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.data.value.immutable.ImmutableLanternEntityToSpawnValue;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.manipulator.DataManipulator;
import org.spongepowered.api.data.manipulator.immutable.ImmutableMobSpawnerData;
import org.spongepowered.api.data.manipulator.mutable.MobSpawnerData;
import org.spongepowered.api.entity.EntityType;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.util.weighted.WeightedEntity;

import java.util.Collection;

import javax.annotation.Nullable;

public class LanternNextEntityToSpawnValue extends LanternValue<WeightedEntity> implements MobSpawnerData.NextEntityToSpawnValue {

    public LanternNextEntityToSpawnValue(WeightedEntity actualValue) {
        super(Keys.SPAWNER_NEXT_ENTITY_TO_SPAWN, new WeightedEntity(EntityTypes.CREEPER, 1), actualValue);
    }

    @Override
    public MobSpawnerData.NextEntityToSpawnValue set(EntityType type, @Nullable Collection<DataManipulator<?, ?>> additionalProperties) {
        if (additionalProperties == null) {
            set(new WeightedEntity(checkNotNull(type), 1));
        } else {
            set(new WeightedEntity(checkNotNull(type), 1, additionalProperties));
        }
        return this;
    }

    @Override
    public ImmutableMobSpawnerData.ImmutableNextEntityToSpawnValue asImmutable() {
        return new ImmutableLanternEntityToSpawnValue(this.actualValue);
    }
}
