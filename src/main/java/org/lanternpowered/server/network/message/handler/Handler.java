package org.lanternpowered.server.network.message.handler;

import org.lanternpowered.server.network.message.Message;
import org.lanternpowered.server.network.session.Session;

public interface Handler<T extends Message> {

    /**
     * Handles a {@link Message} that was received.
     * 
     * @param session the session that received the message
     * @param message the message that was received
     */
    void handle(Session session, T message);
}
