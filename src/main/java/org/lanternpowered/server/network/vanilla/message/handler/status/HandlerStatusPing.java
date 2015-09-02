package org.lanternpowered.server.network.vanilla.message.handler.status;

import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.status.MessageStatusInOutPing;

public final class HandlerStatusPing implements Handler<MessageStatusInOutPing> {

    @Override
    public void handle(Session session, MessageStatusInOutPing message) {
        session.send(message);
    }
}
