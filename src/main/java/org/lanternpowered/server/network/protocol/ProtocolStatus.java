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
import org.lanternpowered.server.network.vanilla.message.codec.status.CodecStatusInOutPing;
import org.lanternpowered.server.network.vanilla.message.codec.status.CodecStatusInRequest;
import org.lanternpowered.server.network.vanilla.message.codec.status.CodecStatusOutResponse;
import org.lanternpowered.server.network.vanilla.message.handler.status.HandlerStatusPing;
import org.lanternpowered.server.network.vanilla.message.handler.status.HandlerStatusRequest;
import org.lanternpowered.server.network.vanilla.message.type.status.MessageStatusInOutPing;
import org.lanternpowered.server.network.vanilla.message.type.status.MessageStatusInRequest;
import org.lanternpowered.server.network.vanilla.message.type.status.MessageStatusOutResponse;

final class ProtocolStatus extends ProtocolBase {

    ProtocolStatus() {
        final MessageRegistry inbound = inbound();
        final MessageRegistry outbound = outbound();

        inbound.bind(CodecStatusInRequest.class, MessageStatusInRequest.class)
                .bindHandler(new HandlerStatusRequest());
        inbound.bind(CodecStatusInOutPing.class, MessageStatusInOutPing.class)
                .bindHandler(new HandlerStatusPing());

        outbound.bind(CodecStatusOutResponse.class, MessageStatusOutResponse.class);
        outbound.bind(CodecStatusInOutPing.class, MessageStatusInOutPing.class);
    }
}
