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
import org.lanternpowered.server.entity.event.EntityEvent;
import org.lanternpowered.server.entity.event.SwingHandEntityEvent;
import org.lanternpowered.server.network.entity.EntityProtocolUpdateContext;
import org.lanternpowered.server.network.entity.parameter.ParameterList;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityStatus;
import org.spongepowered.api.data.type.HandType;
import org.spongepowered.api.data.type.HandTypes;

public class IronGolemEntityProcotol<E extends LanternEntity> extends InsentientEntityProtocol<E> {

    private static final int POPPY_ADD_STATUS = 11;
    private static final int POPPY_REMOVE_STATUS = 34;
    private static final int POPPY_RESEND_DELAY = 300;

    private int lastHoldPoppyTime;

    public IronGolemEntityProcotol(E entity) {
        super(entity);
    }

    @Override
    protected void spawn(ParameterList parameterList) {
        super.spawn(parameterList);
        parameterList.add(EntityParameters.IronGolem.FLAGS, (byte) 0);
    }

    @Override
    protected String getMobType() {
        return "minecraft:iron_golem";
    }

    @Override
    protected void spawn(EntityProtocolUpdateContext context) {
        super.spawn(context);
        if (getEntity().get(LanternKeys.HOLDS_POPPY).orElse(false)) {
            context.sendToAll(() -> new MessagePlayOutEntityStatus(getRootEntityId(), POPPY_ADD_STATUS));
        }
    }

    @Override
    protected void update(EntityProtocolUpdateContext context) {
        super.update(context);
        final boolean holdsPoppy = getEntity().get(LanternKeys.HOLDS_POPPY).orElse(false);
        if (holdsPoppy) {
            this.lastHoldPoppyTime -= getTickRate();
            if (this.lastHoldPoppyTime <= 0) {
                context.sendToAll(() -> new MessagePlayOutEntityStatus(getRootEntityId(), POPPY_ADD_STATUS));
                this.lastHoldPoppyTime = POPPY_RESEND_DELAY;
            }
        } else if (this.lastHoldPoppyTime >= 0) {
            context.sendToAll(() -> new MessagePlayOutEntityStatus(getRootEntityId(), POPPY_REMOVE_STATUS));
            this.lastHoldPoppyTime = -1;
        }
    }

    @Override
    protected void handleEvent(EntityProtocolUpdateContext context, EntityEvent event) {
        if (event instanceof SwingHandEntityEvent) {
            final HandType handType = ((SwingHandEntityEvent) event).getHandType();
            // Doesn't matter which hand type, just play the swing animation,
            // the golem will use both arms at the same time
            if (handType == HandTypes.MAIN_HAND || handType == HandTypes.OFF_HAND) {
                context.sendToAll(() -> new MessagePlayOutEntityStatus(getRootEntityId(), 4));
            }
        } else {
            super.handleEvent(context, event);
        }
    }
}
