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
package org.lanternpowered.server.network.vanilla.message.handler.play;

import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutUnregisterChannels;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.event.SpongeEventFactory;
import org.spongepowered.api.event.cause.Cause;
import org.spongepowered.api.event.cause.EventContext;

import java.util.Set;

public final class HandlerPlayInUnregisterChannels implements Handler<MessagePlayInOutUnregisterChannels> {

    @Override
    public void handle(NetworkContext context, MessagePlayInOutUnregisterChannels message) {
        final Set<String> channels = message.getChannels();
        final Set<String> registeredChannels = context.getSession().getRegisteredChannels();
        final Cause cause = Cause.of(EventContext.empty(), context.getSession().getPlayer(), context.getSession());

        for (String channel : channels) {
            if (registeredChannels.remove(channel)) {
                Sponge.getEventManager().post(SpongeEventFactory.createChannelRegistrationEventUnregister(cause, channel));
            }
        }
    }
}
