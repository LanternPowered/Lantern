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
import org.lanternpowered.server.network.vanilla.packet.codec.DisconnectCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.login.LoginChannelResponseCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.login.LoginEncryptionResponseCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.login.LoginStartCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.login.LoginChannelRequestCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.login.LoginEncryptionRequestCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.login.SetCompressionCodec;
import org.lanternpowered.server.network.vanilla.packet.codec.login.LoginSuccessCodec;
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

        inbound.bind(LoginStartCodec.class, LoginStartPacket.class).bindHandler(new LoginStartHandler());
        inbound.bind(LoginEncryptionResponseCodec.class, LoginEncryptionResponsePacket.class)
                .bindHandler(new LoginEncryptionResponseHandler());
        inbound.bindHandler(LoginFinishPacket.class, new LoginFinishHandler());
        inbound.bind(LoginChannelResponseCodec.class, LoginChannelResponsePacket.class);

        outbound.bind(DisconnectCodec.class, DisconnectPacket.class);
        outbound.bind(LoginEncryptionRequestCodec.class, LoginEncryptionRequestPacket.class);
        outbound.bind(LoginSuccessCodec.class, LoginSuccessPacket.class);
        outbound.bind(SetCompressionCodec.class, SetCompressionPacket.class);
        outbound.bind(LoginChannelRequestCodec.class, LoginChannelRequestPacket.class);
    }
}
