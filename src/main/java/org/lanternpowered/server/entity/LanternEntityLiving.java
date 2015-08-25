package org.lanternpowered.server.entity;

import org.spongepowered.api.data.manipulator.mutable.entity.DamageableData;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.entity.living.Living;

public class LanternEntityLiving extends LanternEntity implements Living {
    protected float headYaw;

    @Override
    public HealthData getHealthData() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public DamageableData getMortalData() {
        // TODO Auto-generated method stub
        return null;
    }

}
