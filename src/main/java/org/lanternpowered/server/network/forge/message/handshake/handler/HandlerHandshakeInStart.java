package org.lanternpowered.server.network.forge.message.handshake.handler;

import io.netty.util.Attribute;

import org.lanternpowered.server.network.forge.message.handshake.HandshakePhase;
import org.lanternpowered.server.network.forge.message.handshake.MessageHandshakeInStart;
import org.lanternpowered.server.network.forge.message.handshake.ServerHandshakePhase;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.session.Session;

public final class HandlerHandshakeInStart implements Handler<MessageHandshakeInStart> {

    @Override
    public void handle(Session session, MessageHandshakeInStart message) {
        Attribute<ServerHandshakePhase> phase = session.getChannel().attr(HandshakePhase.PHASE);
        if (phase.get() != null && phase.get() != ServerHandshakePhase.START) {
            session.disconnect("Retrieved unexpected forge handshake start message.");
            return;
        }
        if (!session.getChannel().attr(Session.FML_MARKER).get()) {
            session.spawnPlayer();
            session.setProtocolState(ProtocolState.PLAY);
        } else {
            phase.set(ServerHandshakePhase.START);
        }
    }
}
