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

import org.lanternpowered.server.network.message.MessageRegistry;
import org.lanternpowered.server.network.vanilla.message.codec.status.StatusPingCodec;
import org.lanternpowered.server.network.vanilla.message.codec.status.StatusRequestCodec;
import org.lanternpowered.server.network.vanilla.message.codec.status.StatusResponseCodec;
import org.lanternpowered.server.network.vanilla.message.handler.status.StatusPingHandler;
import org.lanternpowered.server.network.vanilla.message.handler.status.StatusRequestHandler;
import org.lanternpowered.server.network.vanilla.message.type.status.StatusPingMessage;
import org.lanternpowered.server.network.vanilla.message.type.status.StatusRequestMessage;
import org.lanternpowered.server.network.vanilla.message.type.status.StatusResponseMessage;

final class ProtocolStatus extends ProtocolBase {

    ProtocolStatus() {
        final MessageRegistry inbound = inbound();
        final MessageRegistry outbound = outbound();

        inbound.bind(StatusRequestCodec.class, StatusRequestMessage.class)
                .bindHandler(new StatusRequestHandler());
        inbound.bind(StatusPingCodec.class, StatusPingMessage.class)
                .bindHandler(new StatusPingHandler());

        outbound.bind(StatusResponseCodec.class, StatusResponseMessage.class);
        outbound.bind(StatusPingCodec.class, StatusPingMessage.class);
    }
}
