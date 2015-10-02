package org.lanternpowered.server.network.forge.handshake;

import io.netty.util.AttributeKey;

public interface ForgeHandshakePhase {

    /**
     * The attribute key of the server handshake phase.
     */
    public static final AttributeKey<ForgeServerHandshakePhase> PHASE = AttributeKey.valueOf("fml-handshake-phase");
}
