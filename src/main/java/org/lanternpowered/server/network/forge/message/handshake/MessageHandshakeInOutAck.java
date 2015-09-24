package org.lanternpowered.server.network.forge.message.handshake;

import org.lanternpowered.server.network.message.Message;

public final class MessageHandshakeInOutAck implements Message {

    private final HandshakePhase phase;

    public MessageHandshakeInOutAck(HandshakePhase phase) {
        this.phase = phase;
    }

    public HandshakePhase getPhase() {
        return this.phase;
    }
}
