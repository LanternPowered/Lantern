package org.lanternpowered.server.network.forge.message.handshake.handler;

import io.netty.util.Attribute;

import org.lanternpowered.server.network.forge.message.handshake.HandshakePhase;
import org.lanternpowered.server.network.forge.message.handshake.MessageHandshakeInOutHello;
import org.lanternpowered.server.network.forge.message.handshake.ServerHandshakePhase;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.session.Session;

public final class HandlerHandshakeInHello implements Handler<MessageHandshakeInOutHello> {

    @Override
    public void handle(Session session, MessageHandshakeInOutHello message) {
        Attribute<ServerHandshakePhase> phase = session.getChannel().attr(HandshakePhase.PHASE);
        if (phase.get() != ServerHandshakePhase.HELLO) {
            session.disconnect("Retrieved unexpected forge handshake hello message.");
            return;
        }
    }
}
