package org.lanternpowered.server.network.forge.message.type.handshake;

import static com.google.common.base.Preconditions.checkNotNull;

import org.lanternpowered.server.network.forge.handshake.ForgeClientHandshakePhase;
import org.lanternpowered.server.network.forge.handshake.ForgeHandshakePhase;
import org.lanternpowered.server.network.forge.handshake.ForgeServerHandshakePhase;
import org.lanternpowered.server.network.message.Message;

public final class MessageForgeHandshakeInOutAck implements Message {

    private final ForgeHandshakePhase phase;

    public MessageForgeHandshakeInOutAck(ForgeClientHandshakePhase phase) {
        this((ForgeHandshakePhase) phase);
    }

    public MessageForgeHandshakeInOutAck(ForgeServerHandshakePhase phase) {
        this((ForgeHandshakePhase) phase);
    }

    private MessageForgeHandshakeInOutAck(ForgeHandshakePhase phase) {
        this.phase = checkNotNull(phase, "phase");
    }

    public ForgeHandshakePhase getPhase() {
        return this.phase;
    }
}
