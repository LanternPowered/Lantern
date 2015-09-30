package org.lanternpowered.server.network.forge.message.handshake.handler;

import io.netty.util.Attribute;

import org.lanternpowered.server.network.forge.message.handshake.HandshakePhase;
import org.lanternpowered.server.network.forge.message.handshake.MessageHandshakeInOutModList;
import org.lanternpowered.server.network.forge.message.handshake.ServerHandshakePhase;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.session.Session;

import com.google.common.collect.Maps;

public final class HandlerHandshakeInModList implements Handler<MessageHandshakeInOutModList> {

    @Override
    public void handle(Session session, MessageHandshakeInOutModList message) {
        Attribute<ServerHandshakePhase> phase = session.getChannel().attr(HandshakePhase.PHASE);
        if (phase.get() != ServerHandshakePhase.HELLO) {
            session.disconnect("Retrieved unexpected forge handshake modList message.");
            return;
        }
        // We don't need to validate the mods for now, maybe in the future, just poke back
        // Just use a empty map for now
        session.send(new MessageHandshakeInOutModList(Maps.newHashMap()));
        phase.set(ServerHandshakePhase.WAITINGCACK);
    }
}
