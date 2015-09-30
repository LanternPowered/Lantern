package org.lanternpowered.server.network.forge.message.handshake;

import io.netty.util.AttributeKey;

public interface HandshakePhase {

    /**
     * The attribute key of the server handshake phase.
     */
    public static final AttributeKey<ServerHandshakePhase> PHASE = AttributeKey.valueOf("fml-handshake-phase");
}
