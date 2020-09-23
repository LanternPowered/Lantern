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
import org.lanternpowered.server.network.vanilla.packet.codec.DisconnectEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.login.LoginChannelResponseDecoder;
import org.lanternpowered.server.network.vanilla.packet.codec.login.LoginEncryptionResponseDecoder;
import org.lanternpowered.server.network.vanilla.packet.codec.login.LoginStartDecoder;
import org.lanternpowered.server.network.vanilla.packet.codec.login.LoginChannelRequestEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.login.LoginEncryptionRequestEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.login.SetCompressionEncoder;
import org.lanternpowered.server.network.vanilla.packet.codec.login.LoginSuccessEncoder;
import org.lanternpowered.server.network.vanilla.packet.handler.login.LoginEncryptionResponseHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.login.LoginFinishHandler;
import org.lanternpowered.server.network.vanilla.packet.handler.login.LoginStartHandler;
import org.lanternpowered.server.network.vanilla.packet.type.DisconnectPacket;
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginChannelResponsePacket;
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginEncryptionResponsePacket;
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginFinishPacket;
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginStartPacket;
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginChannelRequestPacket;
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginEncryptionRequestPacket;
import org.lanternpowered.server.network.vanilla.packet.type.login.SetCompressionPacket;
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginSuccessPacket;

final class ProtocolLogin extends ProtocolBase {

    ProtocolLogin() {
        final MessageRegistry inbound = inbound();
        final MessageRegistry outbound = outbound();

        inbound.bind(LoginStartDecoder.class, LoginStartPacket.class).bindHandler(new LoginStartHandler());
        inbound.bind(LoginEncryptionResponseDecoder.class, LoginEncryptionResponsePacket.class)
                .bindHandler(new LoginEncryptionResponseHandler());
        inbound.bindHandler(LoginFinishPacket.class, new LoginFinishHandler());
        inbound.bind(LoginChannelResponseDecoder.class, LoginChannelResponsePacket.class);

        outbound.bind(DisconnectEncoder.class, DisconnectPacket.class);
        outbound.bind(LoginEncryptionRequestEncoder.class, LoginEncryptionRequestPacket.class);
        outbound.bind(LoginSuccessEncoder.class, LoginSuccessPacket.class);
        outbound.bind(SetCompressionEncoder.class, SetCompressionPacket.class);
        outbound.bind(LoginChannelRequestEncoder.class, LoginChannelRequestPacket.class);
    }
}
