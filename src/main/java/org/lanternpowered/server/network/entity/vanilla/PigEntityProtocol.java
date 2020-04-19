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

import org.lanternpowered.server.entity.LanternEntity;
import org.lanternpowered.server.network.entity.parameter.ParameterList;
import org.spongepowered.api.data.Keys;

public class PigEntityProtocol<E extends LanternEntity> extends AnimalEntityProtocol<E> {

    private boolean lastSaddled;

    public PigEntityProtocol(E entity) {
        super(entity);
    }

    @Override
    protected String getMobType() {
        return "minecraft:pig";
    }

    @Override
    protected void spawn(ParameterList parameterList) {
        super.spawn(parameterList);
        parameterList.add(EntityParameters.Pig.HAS_SADDLE, this.entity.get(Keys.PIG_SADDLE).orElse(false));
    }

    @Override
    protected void update(ParameterList parameterList) {
        super.update(parameterList);
        final boolean saddled = this.entity.get(Keys.PIG_SADDLE).orElse(false);
        if (this.lastSaddled != saddled) {
            parameterList.add(EntityParameters.Pig.HAS_SADDLE, saddled);
            this.lastSaddled = saddled;
        }
    }
}
