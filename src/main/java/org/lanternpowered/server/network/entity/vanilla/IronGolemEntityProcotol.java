/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
