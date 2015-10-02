package org.lanternpowered.server.network.forge.message.handler.handshake;

import java.util.List;

import io.netty.util.Attribute;

import org.lanternpowered.server.network.forge.handshake.ForgeClientHandshakePhase;
import org.lanternpowered.server.network.forge.handshake.ForgeHandshakePhase;
import org.lanternpowered.server.network.forge.handshake.ForgeServerHandshakePhase;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutAck;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeOutComplete;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeOutRegistryData;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.session.Session;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class HandlerForgeHandshakeInAck implements Handler<MessageForgeHandshakeInOutAck> {

    @Override
    public void handle(Session session, MessageForgeHandshakeInOutAck message) {
        Attribute<ForgeServerHandshakePhase> phase = session.getChannel().attr(ForgeHandshakePhase.PHASE);
        switch (phase.get()) {
            case WAITINGCACK:
                if (!message.getPhase().equals(ForgeClientHandshakePhase.WAITINGSERVERDATA)) {
                    session.disconnect("Retrieved unexpected forge handshake ack message. (Got " + message.getPhase() +
                            ", expected " + ForgeClientHandshakePhase.WAITINGSERVERDATA + ")");
                } else {
                    List<MessageForgeHandshakeOutRegistryData.Entry> entries = Lists.newArrayList();
                    entries.add(new MessageForgeHandshakeOutRegistryData.Entry("fml:items", Maps.newHashMap(), Lists.newArrayList()));
                    entries.add(new MessageForgeHandshakeOutRegistryData.Entry("fml:blocks", Maps.newHashMap(), Lists.newArrayList()));
                    session.send(new MessageForgeHandshakeOutRegistryData(entries));
                    session.send(new MessageForgeHandshakeInOutAck(ForgeServerHandshakePhase.WAITINGCACK));
                    phase.set(ForgeServerHandshakePhase.COMPLETE);
                }
                break;
            case COMPLETE:
                if (!message.getPhase().equals(ForgeClientHandshakePhase.WAITINGSERVERCOMPLETE)) {
                    session.disconnect("Retrieved unexpected forge handshake ack message. (Got " + message.getPhase() +
                            ", expected " + ForgeClientHandshakePhase.WAITINGSERVERCOMPLETE + ")");
                } else {
                    session.send(new MessageForgeHandshakeInOutAck(ForgeServerHandshakePhase.COMPLETE));
                    phase.set(ForgeServerHandshakePhase.DONE);
                }
                break;
            case DONE:
                if (!message.getPhase().equals(ForgeClientHandshakePhase.PENDINGCOMPLETE) &&
                        !message.getPhase().equals(ForgeClientHandshakePhase.COMPLETE)) {
                    session.disconnect("Retrieved unexpected forge handshake ack message. (Got " + message.getPhase() +
                            ", expected " + ForgeClientHandshakePhase.PENDINGCOMPLETE + " or " +
                            ForgeClientHandshakePhase.COMPLETE + ")");
                } else {
                    if (message.getPhase().equals(ForgeClientHandshakePhase.PENDINGCOMPLETE)) {
                        session.send(new MessageForgeHandshakeInOutAck(ForgeServerHandshakePhase.DONE));
                    } else {
                        session.send(new MessageForgeHandshakeOutComplete());
                        session.setProtocolState(ProtocolState.PLAY);
                        session.spawnPlayer();
                    }
                }
            case ERROR:
                break;
            default:
                session.disconnect("Retrieved unexpected forge handshake ack message. (Got " +
                        message.getPhase() + ")");
        }
    }
}
