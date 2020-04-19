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
import org.lanternpowered.server.network.vanilla.message.codec.connection.CodecOutDisconnect;
import org.lanternpowered.server.network.vanilla.message.codec.login.CodecLoginInChannelResponse;
import org.lanternpowered.server.network.vanilla.message.codec.login.CodecLoginInEncryptionResponse;
import org.lanternpowered.server.network.vanilla.message.codec.login.CodecLoginInStart;
import org.lanternpowered.server.network.vanilla.message.codec.login.CodecLoginOutChannelRequest;
import org.lanternpowered.server.network.vanilla.message.codec.login.CodecLoginOutEncryptionRequest;
import org.lanternpowered.server.network.vanilla.message.codec.login.CodecLoginOutSetCompression;
import org.lanternpowered.server.network.vanilla.message.codec.login.CodecLoginOutSuccess;
import org.lanternpowered.server.network.vanilla.message.handler.login.HandlerEncryptionResponse;
import org.lanternpowered.server.network.vanilla.message.handler.login.HandlerLoginFinish;
import org.lanternpowered.server.network.vanilla.message.handler.login.HandlerLoginStart;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageOutDisconnect;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInChannelResponse;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInEncryptionResponse;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInFinish;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInStart;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutChannelRequest;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutEncryptionRequest;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutSetCompression;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutSuccess;

final class ProtocolLogin extends ProtocolBase {

    ProtocolLogin() {
        final MessageRegistry inbound = inbound();
        final MessageRegistry outbound = outbound();

        inbound.bind(CodecLoginInStart.class, MessageLoginInStart.class).bindHandler(new HandlerLoginStart());
        inbound.bind(CodecLoginInEncryptionResponse.class, MessageLoginInEncryptionResponse.class)
                .bindHandler(new HandlerEncryptionResponse());
        inbound.bindHandler(MessageLoginInFinish.class, new HandlerLoginFinish());
        inbound.bind(CodecLoginInChannelResponse.class, MessageLoginInChannelResponse.class);

        outbound.bind(CodecOutDisconnect.class, MessageOutDisconnect.class);
        outbound.bind(CodecLoginOutEncryptionRequest.class, MessageLoginOutEncryptionRequest.class);
        outbound.bind(CodecLoginOutSuccess.class, MessageLoginOutSuccess.class);
        outbound.bind(CodecLoginOutSetCompression.class, MessageLoginOutSetCompression.class);
        outbound.bind(CodecLoginOutChannelRequest.class, MessageLoginOutChannelRequest.class);
    }
}
