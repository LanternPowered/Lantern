package org.lanternpowered.server.network.forge.message.handler.handshake;

import io.netty.util.Attribute;

import org.lanternpowered.server.network.forge.handshake.ForgeHandshakePhase;
import org.lanternpowered.server.network.forge.handshake.ForgeServerHandshakePhase;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutModList;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.session.Session;

import com.google.common.collect.Maps;

public final class HandlerForgeHandshakeInModList implements Handler<MessageForgeHandshakeInOutModList> {

    @Override
    public void handle(Session session, MessageForgeHandshakeInOutModList message) {
        Attribute<ForgeServerHandshakePhase> phase = session.getChannel().attr(ForgeHandshakePhase.PHASE);
        if (phase.get() != ForgeServerHandshakePhase.HELLO) {
            session.disconnect("Retrieved unexpected forge handshake modList message.");
            return;
        }
        // We don't need to validate the mods for now, maybe in the future, just poke back
        // Just use a empty map for now
        session.send(new MessageForgeHandshakeInOutModList(Maps.newHashMap()));
        phase.set(ForgeServerHandshakePhase.WAITINGCACK);
    }
}
