package org.lanternpowered.server.network.forge.message.handler.handshake;

import io.netty.util.Attribute;

import org.lanternpowered.server.network.forge.handshake.ForgeHandshakePhase;
import org.lanternpowered.server.network.forge.handshake.ForgeServerHandshakePhase;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInStart;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.session.Session;

public final class HandlerForgeHandshakeInStart implements Handler<MessageForgeHandshakeInStart> {

    @Override
    public void handle(Session session, MessageForgeHandshakeInStart message) {
        Attribute<ForgeServerHandshakePhase> phase = session.getChannel().attr(ForgeHandshakePhase.PHASE);
        if (phase.get() != null && phase.get() != ForgeServerHandshakePhase.START) {
            session.disconnect("Retrieved unexpected forge handshake start message.");
            return;
        }
        if (!session.getChannel().attr(Session.FML_MARKER).get()) {
            session.spawnPlayer();
            session.setProtocolState(ProtocolState.PLAY);
        } else {
            phase.set(ForgeServerHandshakePhase.START);
        }
    }
}
