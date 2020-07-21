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
import org.lanternpowered.server.entity.event.EntityEvent;
import org.lanternpowered.server.entity.event.LoveModeEntityEvent;
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutEntityStatus;

public abstract class AnimalEntityProtocol<E extends LanternEntity> extends AgeableEntityProtocol<E> {

    protected AnimalEntityProtocol(E entity) {
        super(entity);
    }

    @Override
    protected void handleEvent(EntityProtocolUpdateContext context, EntityEvent event) {
        if (event instanceof LoveModeEntityEvent) {
            context.sendToAll(() -> new PacketPlayOutEntityStatus(getRootEntityId(), 18));
        } else {
            super.handleEvent(context, event);
        }
    }
}
