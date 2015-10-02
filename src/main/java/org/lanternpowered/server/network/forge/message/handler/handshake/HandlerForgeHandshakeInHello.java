package org.lanternpowered.server.network.forge.message.handler.handshake;

import io.netty.util.Attribute;

import org.lanternpowered.server.network.forge.handshake.ForgeHandshakePhase;
import org.lanternpowered.server.network.forge.handshake.ForgeServerHandshakePhase;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutHello;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.session.Session;

public final class HandlerForgeHandshakeInHello implements Handler<MessageForgeHandshakeInOutHello> {

    @Override
    public void handle(Session session, MessageForgeHandshakeInOutHello message) {
        Attribute<ForgeServerHandshakePhase> phase = session.getChannel().attr(ForgeHandshakePhase.PHASE);
        if (phase.get() != ForgeServerHandshakePhase.HELLO) {
            session.disconnect("Retrieved unexpected forge handshake hello message.");
            return;
        }
    }
}
