package org.lanternpowered.server.network.vanilla.message.handler.play;

import java.util.Set;

import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutUnregisterChannels;

public final class HandlerPlayInUnregisterChannels implements Handler<MessagePlayInOutUnregisterChannels> {

    @Override
    public void handle(Session session, MessagePlayInOutUnregisterChannels message) {
        Set<String> channels = message.getChannels();
        session.getRegisteredChannels().removeAll(channels);
    }
}
