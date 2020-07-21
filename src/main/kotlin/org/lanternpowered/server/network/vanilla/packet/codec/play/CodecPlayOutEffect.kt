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
import org.lanternpowered.server.network.message.Packet
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.PacketPlayOutEffect
import org.lanternpowered.server.network.vanilla.packet.type.play.SetMusicDiscPacket
import org.lanternpowered.server.registry.type.data.MusicDiscRegistry

class CodecPlayOutEffect : Codec<Packet> {

    override fun encode(context: CodecContext, packet: Packet): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            when (packet) {
                is PacketPlayOutEffect -> {
                    writeInt(packet.type)
                    writePosition(packet.position)
                    writeInt(packet.data)
                    writeBoolean(packet.isBroadcast)
                }
                is SetMusicDiscPacket -> {
                    writeInt(1010)
                    writePosition(packet.position)
                    val id = if (packet.musicDisc == null) 0 else 2256 + MusicDiscRegistry.getId(packet.musicDisc)
                    writeInt(id)
                    writeBoolean(false)
                }
                else -> throw EncoderException("Unsupported message type: " + packet.javaClass.name)
            }
        }
    }
}
