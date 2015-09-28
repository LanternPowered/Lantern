package org.lanternpowered.server.network.forge.message.handshake;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.message.Message;

public final class MessageHandshakeInOutAck implements Message {

    private final HandshakePhase phase;

    public MessageHandshakeInOutAck(ClientHandshakePhase phase) {
        this((HandshakePhase) phase);
    }

    public MessageHandshakeInOutAck(ServerHandshakePhase phase) {
        this((HandshakePhase) phase);
    }

    private MessageHandshakeInOutAck(HandshakePhase phase) {
        this.phase = checkNotNull(phase, "phase");
    }

    public HandshakePhase getPhase() {
        return this.phase;
    }
}
