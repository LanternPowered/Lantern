package org.lanternpowered.server.network.forge.message.handler.handshake;

import java.util.Set;

import io.netty.util.Attribute;

import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.network.forge.handshake.ForgeHandshakePhase;
import org.lanternpowered.server.network.forge.handshake.ForgeServerHandshakePhase;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInStart;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.session.Session;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutRegisterChannels;

import com.google.common.collect.Sets;

public final class HandlerForgeHandshakeInStart implements Handler<MessageForgeHandshakeInStart> {

    @Override
    public void handle(Session session, MessageForgeHandshakeInStart message) {
        Attribute<ForgeServerHandshakePhase> phase = session.getChannel().attr(ForgeHandshakePhase.PHASE);
        if (phase.get() != null && phase.get() != ForgeServerHandshakePhase.START) {
            session.disconnect("Retrieved unexpected forge handshake start message.");
            return;
        }
        boolean fml = session.getChannel().attr(Session.FML_MARKER).get();

        Set<String> channels = Sets.newHashSet(LanternGame.get().getServer().getRegisteredChannels());
        if (fml) {
            channels.add("FML");
        }
        if (!channels.isEmpty()) {
            session.send(new MessagePlayInOutRegisterChannels(channels));
        }
        if (fml) {
            phase.set(ForgeServerHandshakePhase.START);
        } else {
            phase.set(ForgeServerHandshakePhase.COMPLETE);
            session.setProtocolState(ProtocolState.PLAY);
            session.spawnPlayer();
        }
    }
}
