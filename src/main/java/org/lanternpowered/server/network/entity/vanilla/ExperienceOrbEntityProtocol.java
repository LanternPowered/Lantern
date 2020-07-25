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
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutDestroyEntities;
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnExperienceOrbPacket;
import org.spongepowered.api.data.Keys;

public class ExperienceOrbEntityProtocol<E extends LanternEntity> extends EntityProtocol<E> {

    private int lastQuantity;

    public ExperienceOrbEntityProtocol(E entity) {
        super(entity);
    }

    @Override
    protected void spawn(EntityProtocolUpdateContext context) {
        this.spawn(context, this.entity.get(Keys.CONTAINED_EXPERIENCE).orElse(1));
    }

    private void spawn(EntityProtocolUpdateContext context, int quantity) {
        if (quantity == 0) {
            context.sendToAll(() -> new PacketPlayOutDestroyEntities(getRootEntityId()));
        } else {
            context.sendToAll(() -> new SpawnExperienceOrbPacket(getRootEntityId(), quantity, this.entity.getPosition()));
        }
    }

    @Override
    protected void update(EntityProtocolUpdateContext context) {
        final int quantity = this.entity.get(Keys.CONTAINED_EXPERIENCE).orElse(1);
        if (this.lastQuantity != quantity) {
            spawn(context, quantity);
            update0(EntityProtocolUpdateContext.empty());
            this.lastQuantity = quantity;
        } else {
            update0(context);
        }
    }

    protected void update0(EntityProtocolUpdateContext context) {
        super.update(context);
    }
}
