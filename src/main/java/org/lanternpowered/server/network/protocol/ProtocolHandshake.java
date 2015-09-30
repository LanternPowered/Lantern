package org.lanternpowered.server.network.protocol;

import org.lanternpowered.server.network.vanilla.message.codec.handshake.CodecHandshakeIn;
import org.lanternpowered.server.network.vanilla.message.handler.handshake.HandlerHandshakeIn;
import org.lanternpowered.server.network.vanilla.message.type.handshake.MessageHandshakeIn;

public final class ProtocolHandshake extends ProtocolBase {

    ProtocolHandshake() {
        this.inbound().register(0x00, MessageHandshakeIn.class, CodecHandshakeIn.class, new HandlerHandshakeIn());
    }
}
