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

public class SnowmanEntityProtocol<E extends LanternEntity> extends InsentientEntityProtocol<E> {

    private boolean lastNoPumpkin;

    public SnowmanEntityProtocol(E entity) {
        super(entity);
    }

    @Override
    protected String getMobType() {
        return "minecraft:snow_golem";
    }

    @Override
    protected void spawn(ParameterList parameterList) {
        parameterList.add(EntityParameters.Snowman.FLAGS, (byte) (this.entity.get(LanternKeys.HAS_PUMPKIN_HEAD).orElse(true) ? 0 : 0x10));
    }

    @Override
    protected void update(ParameterList parameterList) {
        final boolean noPumpkin = !this.entity.get(LanternKeys.HAS_PUMPKIN_HEAD).orElse(true);
        if (this.lastNoPumpkin != noPumpkin) {
            parameterList.add(EntityParameters.Snowman.FLAGS, (byte) (noPumpkin ? 0x10 : 0));
            this.lastNoPumpkin = noPumpkin;
        }
    }
}
