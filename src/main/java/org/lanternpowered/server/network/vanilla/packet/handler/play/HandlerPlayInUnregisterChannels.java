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
package org.lanternpowered.server.network.vanilla.packet.handler.play;

import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.packet.handler.Handler;
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayInOutUnregisterChannels;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;

import java.util.Set;

public final class HandlerPlayInUnregisterChannels implements Handler<PacketPlayInOutUnregisterChannels> {

    @Override
    public void handle(NetworkContext context, PacketPlayInOutUnregisterChannels packet) {
        final Set<String> channels = packet.getChannels();
        final Set<String> registeredChannels = context.getSession().getRegisteredChannels();
        final Cause cause = Cause.of(EventContext.empty(), context.getSession().getPlayer(), context.getSession());

        for (String channel : channels) {
            if (registeredChannels.remove(channel)) {
                Sponge.getEventManager().post(SpongeEventFactory.createChannelRegistrationEventUnregister(cause, channel));
            }
        }
    }
}
