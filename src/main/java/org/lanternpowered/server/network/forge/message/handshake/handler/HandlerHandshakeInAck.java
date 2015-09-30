package org.lanternpowered.server.network.forge.message.handshake.handler;

import java.util.List;

import io.netty.util.Attribute;

import org.lanternpowered.server.network.forge.message.handshake.ClientHandshakePhase;
import org.lanternpowered.server.network.forge.message.handshake.HandshakePhase;
import org.lanternpowered.server.network.forge.message.handshake.MessageHandshakeInOutAck;
import org.lanternpowered.server.network.forge.message.handshake.MessageHandshakeOutComplete;
import org.lanternpowered.server.network.forge.message.handshake.MessageHandshakeOutRegistryData;
import org.lanternpowered.server.network.forge.message.handshake.ServerHandshakePhase;
import org.lanternpowered.server.network.message.handler.Handler;
import org.lanternpowered.server.network.protocol.ProtocolState;
import org.lanternpowered.server.network.session.Session;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public final class HandlerHandshakeInAck implements Handler<MessageHandshakeInOutAck> {

    @Override
    public void handle(Session session, MessageHandshakeInOutAck message) {
        Attribute<ServerHandshakePhase> phase = session.getChannel().attr(HandshakePhase.PHASE);
        switch (phase.get()) {
            case WAITINGCACK:
                if (!message.getPhase().equals(ClientHandshakePhase.WAITINGSERVERDATA)) {
                    session.disconnect("Retrieved unexpected forge handshake ack message. (Got " + message.getPhase() +
                            ", expected " + ClientHandshakePhase.WAITINGSERVERDATA + ")");
                } else {
                    List<MessageHandshakeOutRegistryData.Entry> entries = Lists.newArrayList();
                    entries.add(new MessageHandshakeOutRegistryData.Entry("fml:items", Maps.newHashMap(), Lists.newArrayList()));
                    entries.add(new MessageHandshakeOutRegistryData.Entry("fml:blocks", Maps.newHashMap(), Lists.newArrayList()));
                    session.send(new MessageHandshakeOutRegistryData(entries));
                    session.send(new MessageHandshakeInOutAck(ServerHandshakePhase.WAITINGCACK));
                    phase.set(ServerHandshakePhase.COMPLETE);
                }
                break;
            case COMPLETE:
                if (!message.getPhase().equals(ClientHandshakePhase.WAITINGSERVERCOMPLETE)) {
                    session.disconnect("Retrieved unexpected forge handshake ack message. (Got " + message.getPhase() +
                            ", expected " + ClientHandshakePhase.WAITINGSERVERCOMPLETE + ")");
                } else {
                    session.send(new MessageHandshakeInOutAck(ServerHandshakePhase.COMPLETE));
                    phase.set(ServerHandshakePhase.DONE);
                }
                break;
            case DONE:
                if (!message.getPhase().equals(ClientHandshakePhase.PENDINGCOMPLETE) &&
                        !message.getPhase().equals(ClientHandshakePhase.COMPLETE)) {
                    session.disconnect("Retrieved unexpected forge handshake ack message. (Got " + message.getPhase() +
                            ", expected " + ClientHandshakePhase.PENDINGCOMPLETE + " or " +
                            ClientHandshakePhase.COMPLETE + ")");
                } else {
                    if (message.getPhase().equals(ClientHandshakePhase.PENDINGCOMPLETE)) {
                        session.send(new MessageHandshakeInOutAck(ServerHandshakePhase.DONE));
                    } else {
                        session.send(new MessageHandshakeOutComplete());
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
