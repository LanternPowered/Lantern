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
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.EffectPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SetMusicDiscPacket
import org.lanternpowered.server.registry.type.data.MusicDiscRegistry

object EffectEncoder : PacketEncoder<Packet> {

    override fun encode(ctx: CodecContext, packet: Packet): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        when (packet) {
            is EffectPacket -> {
                buf.writeInt(packet.type)
                buf.writeBlockPosition(packet.position)
                buf.writeInt(packet.data)
                buf.writeBoolean(packet.isBroadcast)
            }
            is SetMusicDiscPacket -> {
                buf.writeInt(1010)
                buf.writeBlockPosition(packet.position)
                val id = if (packet.musicDisc == null) 0 else 2256 + MusicDiscRegistry.getId(packet.musicDisc)
                buf.writeInt(id)
                buf.writeBoolean(false)
            }
            else -> throw EncoderException("Unsupported message type: " + packet.javaClass.name)
        }
        return buf
    }
}
