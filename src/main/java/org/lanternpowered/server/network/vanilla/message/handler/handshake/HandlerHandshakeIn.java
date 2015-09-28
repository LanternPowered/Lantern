package org.lanternpowered.server.network.vanilla.message.handler.handshake;

import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.game.LanternMinecraftVersion;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.handshake.MessageHandshakeIn;

public class HandlerHandshakeIn implements Handler<MessageHandshakeIn> {

    @Override
    public void handle(Session session, MessageHandshakeIn message) {
        ProtocolState next = ProtocolState.fromId(message.getNextState());
        if (next == null) {
            session.disconnect("Unknown protocol state! (" + message.getNextState() + ")");
        }

        session.setProtocolState(next);
        if (!next.equals(ProtocolState.LOGIN) && !next.equals(ProtocolState.STATUS)) {
            session.disconnect("Received a unexpected handshake message! (" + next + ")");
        }
        // session.setVirtualHost(message.getAddress());
        session.setProtocolVersion(message.getProtocolVersion());
        session.getChannel().attr(Session.FML_MARKER).set(message.hasFMLMarker());

        if (next == ProtocolState.LOGIN) {
            int protocol = ((LanternMinecraftVersion) LanternGame.get().getPlatform().getMinecraftVersion()).getProtocol();

            if (message.getProtocolVersion() < protocol) {
                session.disconnect("Outdated client! I'm running " + LanternGame.get().getPlatform().getMinecraftVersion().getName());
            } else if (message.getProtocolVersion() > protocol) {
                session.disconnect("Outdated server! I'm running " + LanternGame.get().getPlatform().getMinecraftVersion().getName());
            }
        }
    }
}
