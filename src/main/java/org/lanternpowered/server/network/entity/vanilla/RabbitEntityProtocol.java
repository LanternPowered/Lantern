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
import org.lanternpowered.server.registry.type.data.RabbitTypeRegistry;
import org.spongepowered.api.data.Keys;
import org.spongepowered.api.data.type.RabbitTypes;

public class RabbitEntityProtocol<E extends LanternEntity> extends AnimalEntityProtocol<E> {

    private int lastType;

    public RabbitEntityProtocol(E entity) {
        super(entity);
    }

    private int getTypeId() {
        return RabbitTypeRegistry.get().getId(this.entity.get(Keys.RABBIT_TYPE).orElseGet(RabbitTypes.WHITE));
    }

    @Override
    protected String getMobType() {
        return "minecraft:rabbit";
    }

    @Override
    protected void spawn(ParameterList parameterList) {
        super.spawn(parameterList);
        parameterList.add(EntityParameters.Rabbit.VARIANT, getTypeId());
    }

    @Override
    protected void update(ParameterList parameterList) {
        super.update(parameterList);
        final int type = this.getTypeId();
        if (type != this.lastType) {
            parameterList.add(EntityParameters.Rabbit.VARIANT, type);
            this.lastType = type;
        }
    }
}
