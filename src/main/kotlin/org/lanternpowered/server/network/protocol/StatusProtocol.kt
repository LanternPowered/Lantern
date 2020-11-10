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

import org.lanternpowered.server.network.vanilla.packet.codec.status.StatusPingCodec
import org.lanternpowered.server.network.vanilla.packet.codec.status.StatusRequestDecoder
import org.lanternpowered.server.network.vanilla.packet.codec.status.StatusResponseEncoder
import org.lanternpowered.server.network.vanilla.packet.handler.status.StatusPingHandler
import org.lanternpowered.server.network.vanilla.packet.handler.status.StatusRequestHandler
import org.lanternpowered.server.network.vanilla.packet.type.status.StatusPingPacket
import org.lanternpowered.server.network.vanilla.packet.type.status.StatusRequestPacket
import org.lanternpowered.server.network.vanilla.packet.type.status.StatusResponsePacket

val StatusProtocol = protocol {
    inbound {
        bind().decoder(StatusRequestDecoder)
        bind().decoder(StatusPingCodec)

        type(StatusRequestPacket::class).handler(StatusRequestHandler)
        type(StatusPingPacket::class).handler(StatusPingHandler)
    }
    outbound {
        bind().encoder(StatusResponseEncoder).accept(StatusResponsePacket::class)
        bind().encoder(StatusPingCodec).accept(StatusPingPacket::class)
    }
}
