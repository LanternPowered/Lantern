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

import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.NetworkSession;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInRequestStatistics;

public class HandlerPlayInRequestStatistics implements Handler<MessagePlayInRequestStatistics> {

    @Override
    public void handle(NetworkContext context, MessagePlayInRequestStatistics message) {
        final NetworkSession session = context.getSession();
        // TODO: Update statistics protocol
        // session.send(session.getPlayer().getStatisticMap().createStatisticsMessage());
    }
}
