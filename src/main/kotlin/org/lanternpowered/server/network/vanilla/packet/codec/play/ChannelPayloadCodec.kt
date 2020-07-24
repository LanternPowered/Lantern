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

import io.netty.handler.codec.EncoderException
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.BrandPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.ChannelPayloadPacket

class ChannelPayloadCodec : AbstractCodecPlayInOutCustomPayload() {

    override fun encode0(context: CodecContext, packet: Packet): MessageResult {
        if (packet is BrandPacket) {
            val content = context.byteBufAlloc().buffer().writeString(packet.brand)
            return MessageResult("minecraft:brand", content)
        }
        throw EncoderException("Unsupported message type: $packet")
    }

    override fun decode0(context: CodecContext, channel: String, content: ByteBuffer): Packet {
        if (channel == "minecraft:brand")
            return BrandPacket(content.readString())
        content.retain() // Retain the content until we can process it
        return ChannelPayloadPacket(channel, content)
    }
}
