package org.lanternpowered.server.network.vanilla.message.handler.play;

import java.util.Set;

import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutRegisterChannels;

public final class HandlerPlayInRegisterChannels implements Handler<MessagePlayInOutRegisterChannels> {

    @Override
    public void handle(Session session, MessagePlayInOutRegisterChannels message) {
        Set<String> channels = message.getChannels();
        session.getRegisteredChannels().addAll(channels);
    }
}
