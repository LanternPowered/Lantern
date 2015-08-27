package org.lanternpowered.server.entity;

import org.spongepowered.api.data.manipulator.mutable.entity.DamageableData;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.entity.living.Living;

public class LanternEntityLiving extends LanternEntity implements Living {

    @Override
    public HealthData getHealthData() {
        return this.get(HealthData.class).get();
    }

    @Override
    public DamageableData getMortalData() {
        return this.get(DamageableData.class).get();
    }

}
