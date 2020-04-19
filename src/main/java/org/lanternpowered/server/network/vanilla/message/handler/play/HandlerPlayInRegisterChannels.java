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
package org.lanternpowered.server.network.vanilla.message.handler.play;

import org.lanternpowered.api.cause.CauseStack;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutRegisterChannels;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;

import java.util.Set;

public final class HandlerPlayInRegisterChannels implements Handler<MessagePlayInOutRegisterChannels> {

    @Override
    public void handle(NetworkContext context, MessagePlayInOutRegisterChannels message) {
        final Set<String> channels = message.getChannels();
        final Set<String> registeredChannels = context.getSession().getRegisteredChannels();

        final CauseStack causeStack = CauseStack.current();
        causeStack.pushCause(context.getSession());
        causeStack.pushCause(context.getSession().getPlayer());
        final Cause cause = causeStack.getCurrentCause();
        for (String channel : channels) {
            if (registeredChannels.add(channel)) {
                Sponge.getEventManager().post(SpongeEventFactory.createChannelRegistrationEventRegister(cause, channel));
            }
        }
        causeStack.popCauses(2);
    }
}
