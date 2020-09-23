/*
 * Lantern
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * This work is licensed under the terms of the MIT License (MIT). For
 * a copy, see 'LICENSE.txt' or <https://opensource.org/licenses/MIT>.
 */
package org.lanternpowered.server.network.protocol;

import org.lanternpowered.server.network.packet.MessageRegistry;
import org.lanternpowered.server.network.vanilla.packet.codec.status.StatusPingCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.status.StatusRequestDecoder;
import org.lanternpowered.server.network.vanilla.packet.codec.status.StatusResponseEncoder;
import org.lanternpowered.server.network.vanilla.packet.handler.status.StatusPingHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.status.StatusRequestHandler;
import org.lanternpowered.server.network.vanilla.packet.type.status.StatusPingPacket;
import org.lanternpowered.server.network.vanilla.packet.type.status.StatusRequestPacket;
import org.lanternpowered.server.network.vanilla.packet.type.status.StatusResponsePacket;

final class ProtocolStatus extends ProtocolBase {

    ProtocolStatus() {
        final MessageRegistry inbound = inbound();
        final MessageRegistry outbound = outbound();

        inbound.bind(StatusRequestDecoder.class, StatusRequestPacket.class)
                .bindHandler(new StatusRequestHandler());
        inbound.bind(StatusPingCodec.class, StatusPingPacket.class)
                .bindHandler(new StatusPingHandler());

        outbound.bind(StatusResponseEncoder.class, StatusResponsePacket.class);
        outbound.bind(StatusPingCodec.class, StatusPingPacket.class);
    }
}
