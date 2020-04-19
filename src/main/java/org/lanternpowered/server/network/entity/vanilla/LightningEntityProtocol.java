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
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext;
import org.lanternpowered.server.network.entity.parameter.ParameterList;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnThunderbolt;

public class LightningEntityProtocol<E extends LanternEntity> extends EntityProtocol<E> {

    public LightningEntityProtocol(E entity) {
        super(entity);
        setTrackingRange(512);
    }

    @Override
    protected void spawn(EntityProtocolUpdateContext context) {
        context.sendToAllExceptSelf(new MessagePlayOutSpawnThunderbolt(getRootEntityId(), this.entity.getPosition()));
    }

    @Override
    protected void spawn(ParameterList parameterList) {
    }

    @Override
    protected void update(ParameterList parameterList) {
    }
}
