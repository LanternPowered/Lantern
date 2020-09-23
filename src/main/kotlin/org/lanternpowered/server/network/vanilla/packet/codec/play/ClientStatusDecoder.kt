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
package org.lanternpowered.server.network.vanilla.packet.codec.play

import io.netty.handler.codec.CodecException
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.packet.PacketDecoder
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientRequestRespawnPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientRequestStatisticsPacket

object ClientStatusDecoder : PacketDecoder<Packet> {

    override fun decode(ctx: CodecContext, buf: ByteBuffer): Packet {
        return when (val action = buf.readVarInt()) {
            0 -> ClientRequestRespawnPacket
            1 -> ClientRequestStatisticsPacket
            else -> throw CodecException("Received client status message with unknown action: $action")
        }
    }
}
