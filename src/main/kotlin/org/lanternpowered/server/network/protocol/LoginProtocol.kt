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
package org.lanternpowered.server.network.protocol

import org.lanternpowered.server.network.vanilla.packet.codec.DisconnectEncoder
import org.lanternpowered.server.network.vanilla.packet.codec.login.LoginChannelRequestEncoder
import org.lanternpowered.server.network.vanilla.packet.codec.login.LoginChannelResponseDecoder
import org.lanternpowered.server.network.vanilla.packet.codec.login.LoginEncryptionRequestEncoder
import org.lanternpowered.server.network.vanilla.packet.codec.login.LoginEncryptionResponseDecoder
import org.lanternpowered.server.network.vanilla.packet.codec.login.LoginStartDecoder
import org.lanternpowered.server.network.vanilla.packet.codec.login.LoginSuccessEncoder
import org.lanternpowered.server.network.vanilla.packet.codec.login.SetCompressionEncoder
import org.lanternpowered.server.network.vanilla.packet.handler.login.LoginEncryptionResponseHandler
import org.lanternpowered.server.network.vanilla.packet.handler.login.LoginFinishHandler
import org.lanternpowered.server.network.vanilla.packet.handler.login.LoginStartHandler
import org.lanternpowered.server.network.vanilla.packet.type.DisconnectPacket
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginChannelRequestPacket
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginChannelResponsePacket
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginEncryptionRequestPacket
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginEncryptionResponsePacket
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginFinishPacket
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginStartPacket
import org.lanternpowered.server.network.vanilla.packet.type.login.LoginSuccessPacket
import org.lanternpowered.server.network.vanilla.packet.type.login.SetCompressionPacket

val LoginProtocol = protocol {
    inbound {
        bind(LoginStartPacket::class).decoder(LoginStartDecoder).handler(LoginStartHandler)
        bind(LoginEncryptionResponsePacket::class).decoder(LoginEncryptionResponseDecoder).handler(LoginEncryptionResponseHandler)
        bind(LoginChannelResponsePacket::class).decoder(LoginChannelResponseDecoder)

        bind(LoginFinishPacket::class).handler(LoginFinishHandler)
    }
    outbound {
        bind(DisconnectPacket::class).encoder(DisconnectEncoder)
        bind(LoginEncryptionRequestPacket::class).encoder(LoginEncryptionRequestEncoder)
        bind(LoginSuccessPacket::class).encoder(LoginSuccessEncoder)
        bind(SetCompressionPacket::class).encoder(SetCompressionEncoder)
        bind(LoginChannelRequestPacket::class).encoder(LoginChannelRequestEncoder)
    }
}
