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

public class ArmorStandEntityProtocol<E extends LanternEntity> extends CreatureEntityProtocol<E> {

    public ArmorStandEntityProtocol(E entity) {
        super(entity);
    }

    @Override
    protected String getMobType() {
        return "minecraft:armor_stand";
    }

    @Override
    protected void spawn(ParameterList parameterList) {
        super.spawn(parameterList);
    }

    @Override
    protected void update(ParameterList parameterList) {
        super.update(parameterList);
    }
}
