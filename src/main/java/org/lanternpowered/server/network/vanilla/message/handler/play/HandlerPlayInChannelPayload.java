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

import org.lanternpowered.server.game.Lantern;
import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.vanilla.message.type.play.ChannelPayloadMessage;

public final class HandlerPlayInChannelPayload implements Handler<ChannelPayloadMessage> {

    @Override
    public void handle(NetworkContext context, ChannelPayloadMessage message) {
        Lantern.getGame().getChannelRegistrar().handlePayload(message.getContent(),
                message.getChannel(), context.getSession());
    }
}
