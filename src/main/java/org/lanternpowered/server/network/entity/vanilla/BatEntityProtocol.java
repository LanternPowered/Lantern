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

public class BatEntityProtocol<E extends LanternEntity> extends InsentientEntityProtocol<E> {

    private boolean lastHanging;

    public BatEntityProtocol(E entity) {
        super(entity);
    }

    @Override
    protected String getMobType() {
        return "minecraft:bat";
    }

    @Override
    protected void spawn(ParameterList parameterList) {
        parameterList.add(EntityParameters.Bat.FLAGS, (byte) (this.entity.get(LanternKeys.IS_HANGING).orElse(false) ? 0x1 : 0));
    }

    @Override
    protected void update(ParameterList parameterList) {
        final boolean hanging = this.entity.get(LanternKeys.IS_HANGING).orElse(false);
        if (this.lastHanging != hanging) {
            parameterList.add(EntityParameters.Bat.FLAGS, (byte) (hanging ? 0x1 : 0));
            this.lastHanging = hanging;
        }
    }
}
