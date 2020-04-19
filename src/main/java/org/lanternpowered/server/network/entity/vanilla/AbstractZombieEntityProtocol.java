/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.network.entity.vanilla;

import org.lanternpowered.server.data.key.LanternKeys;
import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.network.entity.parameter.ParameterList;

public abstract class AbstractZombieEntityProtocol<E extends LanternEntity> extends AgeableEntityProtocol<E> {

    private boolean lastAreHandsUp;

    public AbstractZombieEntityProtocol(E entity) {
        super(entity);
    }

    private boolean areHandsUp() {
        return this.entity.get(LanternKeys.ARE_HANDS_UP).orElse(false);
    }

    @Override
    protected void spawn(ParameterList parameterList) {
        super.spawn(parameterList);
        parameterList.add(EntityParameters.AbstractZombie.UNUSED, 0);
        parameterList.add(EntityParameters.AbstractZombie.HANDS_UP, areHandsUp());
    }

    @Override
    protected void update(ParameterList parameterList) {
        super.update(parameterList);
        final boolean handsUp = this.areHandsUp();
        if (handsUp != this.lastAreHandsUp) {
            parameterList.add(EntityParameters.AbstractZombie.HANDS_UP, handsUp);
            this.lastAreHandsUp = handsUp;
        }
    }

    @Override
    protected boolean hasEquipment() {
        return true;
    }
}
