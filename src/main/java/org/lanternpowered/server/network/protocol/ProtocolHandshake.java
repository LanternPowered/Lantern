package org.lanternpowered.server.network.protocol;

import org.lanternpowered.server.network.vanilla.handler.handshake.HandlerHandshakeIn;
import org.lanternpowered.server.network.vanilla.message.codec.handshake.CodecHandshakeIn;
import org.lanternpowered.server.network.vanilla.message.type.handshake.MessageHandshakeIn;

public class ProtocolHandshake extends ProtocolBase {

    public ProtocolHandshake() {
        this.inbound().register(0x00, MessageHandshakeIn.class, CodecHandshakeIn.class, new HandlerHandshakeIn());
    }
}
