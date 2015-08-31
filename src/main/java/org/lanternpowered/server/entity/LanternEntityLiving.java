package org.lanternpowered.server.entity;

import org.spongepowered.api.data.manipulator.mutable.entity.DamageableData;
import org.spongepowered.api.data.manipulator.mutable.entity.HealthData;
import org.spongepowered.api.entity.living.Living;
import org.spongepowered.api.text.Text;

public class LanternEntityLiving extends LanternEntity implements Living {

    @Override
    public HealthData getHealthData() {
        return this.get(HealthData.class).get();
    }

    @Override
    public DamageableData getMortalData() {
        return this.get(DamageableData.class).get();
    }

    @Override
    public Text getTeamRepresentation() {
        // TODO Auto-generated method stub
        return null;
    }

}
