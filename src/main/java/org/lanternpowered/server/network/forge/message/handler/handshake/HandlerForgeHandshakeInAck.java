package org.lanternpowered.server.network.forge.message.handler.handshake;

import java.util.List;

import io.netty.util.Attribute;

import org.lanternpowered.server.game.LanternGame;
import org.lanternpowered.server.network.forge.handshake.ForgeClientHandshakePhase;
import org.lanternpowered.server.network.forge.handshake.ForgeHandshakePhase;
import org.lanternpowered.server.network.forge.handshake.ForgeServerHandshakePhase;
import org.lanternpowered.server.network.forge.message.type.handshake.MessageForgeHandshakeInOutAck;
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
            case WAITING_ACK:
                if (!message.getPhase().equals(ForgeClientHandshakePhase.WAITING_SERVER_DATA)) {
                    session.disconnect("Retrieved unexpected forge handshake ack message. (Got " + message.getPhase() +
                            ", expected " + ForgeClientHandshakePhase.WAITING_SERVER_DATA + ")");
                } else {
                    List<MessageForgeHandshakeOutRegistryData.Entry> entries = Lists.newArrayList();
                    entries.add(new MessageForgeHandshakeOutRegistryData.Entry("fml:items", Maps.newHashMap(), Lists.newArrayList()));
                    entries.add(new MessageForgeHandshakeOutRegistryData.Entry("fml:blocks", Maps.newHashMap(), Lists.newArrayList()));
                    session.send(new MessageForgeHandshakeOutRegistryData(entries));
                    session.send(new MessageForgeHandshakeInOutAck(ForgeServerHandshakePhase.WAITING_ACK));
                    phase.set(ForgeServerHandshakePhase.COMPLETE);
                }
                LanternGame.log().info("{}: Forge handshake -> Received ack (waitingServerData) message.", session.getGameProfile().getName());
                break;
            case COMPLETE:
                if (!message.getPhase().equals(ForgeClientHandshakePhase.WAITING_SERVER_COMPLETE)) {
                    session.disconnect("Retrieved unexpected forge handshake ack message. (Got " + message.getPhase() +
                            ", expected " + ForgeClientHandshakePhase.WAITING_SERVER_COMPLETE + ")");
                } else {
                    session.send(new MessageForgeHandshakeInOutAck(ForgeServerHandshakePhase.COMPLETE));
                    phase.set(ForgeServerHandshakePhase.DONE);
                }
                LanternGame.log().info("{}: Forge handshake -> Received ack (waitingServerComplete) message.", session.getGameProfile().getName());
                break;
            case DONE:
                if (!message.getPhase().equals(ForgeClientHandshakePhase.PENDING_COMPLETE) &&
                        !message.getPhase().equals(ForgeClientHandshakePhase.COMPLETE)) {
                    session.disconnect("Retrieved unexpected forge handshake ack message. (Got " + message.getPhase() +
                            ", expected " + ForgeClientHandshakePhase.PENDING_COMPLETE + " or " +
                            ForgeClientHandshakePhase.COMPLETE + ")");
                } else {
                    if (message.getPhase().equals(ForgeClientHandshakePhase.PENDING_COMPLETE)) {
                        session.send(new MessageForgeHandshakeInOutAck(ForgeServerHandshakePhase.DONE));
                        LanternGame.log().info("{}: Forge handshake -> Received ack (pendingComplete) message.", session.getGameProfile().getName());
                    } else {
                        session.setProtocolState(ProtocolState.PLAY);
                        session.spawnPlayer();
                        LanternGame.log().info("{}: Forge handshake -> Received ack (complete) message.", session.getGameProfile().getName());
                    }
                }
                break;
            case ERROR:
                break;
            default:
                session.disconnect("Retrieved unexpected forge handshake ack message. (Got " +
                        message.getPhase() + ")");
        }
    }
}
