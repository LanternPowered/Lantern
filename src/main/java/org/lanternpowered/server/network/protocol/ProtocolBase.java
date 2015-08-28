package org.lanternpowered.server.network.protocol;

import org.lanternpowered.server.network.message.LanternMessageRegistry;
import org.lanternpowered.server.network.message.MessageRegistry;

public class ProtocolBase implements Protocol {

    private final MessageRegistry inbound = new LanternMessageRegistry();
    private final MessageRegistry outbound = new LanternMessageRegistry();

    @Override
    public MessageRegistry inbound() {
        return this.inbound;
    }

    @Override
    public MessageRegistry outbound() {
        return this.outbound;
    }
}
