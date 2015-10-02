package org.lanternpowered.server.network.vanilla.message.handler.connection;

import org.lanternpowered.server.network.message.Async;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageInOutPing;

@Async
public final class HandlerInPing implements Handler<MessageInOutPing> {

    @Override
    public void handle(Session session, MessageInOutPing message) {
        session.pong(message.getKeepAliveId());
    }
}
