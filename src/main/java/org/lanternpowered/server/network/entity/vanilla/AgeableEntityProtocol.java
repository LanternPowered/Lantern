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
import org.spongepowered.api.data.Keys;

public abstract class AgeableEntityProtocol<E extends LanternEntity> extends InsentientEntityProtocol<E> {

    private boolean lastIsBaby;

    public AgeableEntityProtocol(E entity) {
        super(entity);
    }

    private boolean isBaby() {
        return this.entity.get(LanternKeys.IS_BABY)
                .orElseGet(() -> this.entity.get(Keys.AGE).orElse(0) < 0);
    }

    @Override
    protected void spawn(ParameterList parameterList) {
        super.spawn(parameterList);
        parameterList.add(EntityParameters.Ageable.IS_BABY, this.isBaby());
    }

    @Override
    protected void update(ParameterList parameterList) {
        super.spawn(parameterList);

        final boolean isBaby = this.isBaby();
        if (isBaby != this.lastIsBaby) {
            parameterList.add(EntityParameters.Ageable.IS_BABY, isBaby);
            this.lastIsBaby = isBaby;
        }
    }
}
