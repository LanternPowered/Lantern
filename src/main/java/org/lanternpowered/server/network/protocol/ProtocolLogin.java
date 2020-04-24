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
import org.lanternpowered.server.network.vanilla.message.codec.DisconnectCodec;
import org.lanternpowered.server.network.vanilla.message.codec.login.LoginChannelResponseCodec;
import org.lanternpowered.server.network.vanilla.message.codec.login.LoginEncryptionResponseCodec;
import org.lanternpowered.server.network.vanilla.message.codec.login.LoginStartCodec;
import org.lanternpowered.server.network.vanilla.message.codec.login.LoginChannelRequestCodec;
import org.lanternpowered.server.network.vanilla.message.codec.login.LoginEncryptionRequestCodec;
import org.lanternpowered.server.network.vanilla.message.codec.login.SetCompressionCodec;
import org.lanternpowered.server.network.vanilla.message.codec.login.LoginSuccessCodec;
import org.lanternpowered.server.network.vanilla.message.handler.login.LoginEncryptionResponseHandler;
import org.lanternpowered.server.network.vanilla.message.handler.login.LoginFinishHandler;
import org.lanternpowered.server.network.vanilla.message.handler.login.LoginStartHandler;
import org.lanternpowered.server.network.vanilla.message.type.DisconnectMessage;
import org.lanternpowered.server.network.vanilla.message.type.login.LoginChannelResponseMessage;
import org.lanternpowered.server.network.vanilla.message.type.login.LoginEncryptionResponseMessage;
import org.lanternpowered.server.network.vanilla.message.type.login.LoginFinishMessage;
import org.lanternpowered.server.network.vanilla.message.type.login.LoginStartMessage;
import org.lanternpowered.server.network.vanilla.message.type.login.LoginChannelRequestMessage;
import org.lanternpowered.server.network.vanilla.message.type.login.LoginEncryptionRequestMessage;
import org.lanternpowered.server.network.vanilla.message.type.login.SetCompressionMessage;
import org.lanternpowered.server.network.vanilla.message.type.login.LoginSuccessMessage;

final class ProtocolLogin extends ProtocolBase {

    ProtocolLogin() {
        final MessageRegistry inbound = inbound();
        final MessageRegistry outbound = outbound();

        inbound.bind(LoginStartCodec.class, LoginStartMessage.class).bindHandler(new LoginStartHandler());
        inbound.bind(LoginEncryptionResponseCodec.class, LoginEncryptionResponseMessage.class)
                .bindHandler(new LoginEncryptionResponseHandler());
        inbound.bindHandler(LoginFinishMessage.class, new LoginFinishHandler());
        inbound.bind(LoginChannelResponseCodec.class, LoginChannelResponseMessage.class);

        outbound.bind(DisconnectCodec.class, DisconnectMessage.class);
        outbound.bind(LoginEncryptionRequestCodec.class, LoginEncryptionRequestMessage.class);
        outbound.bind(LoginSuccessCodec.class, LoginSuccessMessage.class);
        outbound.bind(SetCompressionCodec.class, SetCompressionMessage.class);
        outbound.bind(LoginChannelRequestCodec.class, LoginChannelRequestMessage.class);
    }
}
