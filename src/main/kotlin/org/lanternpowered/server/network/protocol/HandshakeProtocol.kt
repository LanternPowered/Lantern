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

import org.lanternpowered.server.network.vanilla.packet.codec.handshake.HandshakeDecoder
import org.lanternpowered.server.network.vanilla.packet.handler.handshake.HandshakeHandler
import org.lanternpowered.server.network.vanilla.packet.type.handshake.HandshakePacket

val HandshakeProtocol = protocol {
    inbound {
        bind().decoder(HandshakeDecoder)

        type(HandshakePacket::class).handler(HandshakeHandler)
    }
}
