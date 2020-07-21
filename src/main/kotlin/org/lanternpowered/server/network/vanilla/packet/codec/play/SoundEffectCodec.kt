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

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.SoundEffectPacket

class SoundEffectCodec : Codec<SoundEffectPacket> {

    override fun encode(context: CodecContext, packet: SoundEffectPacket): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            writeVarInt(packet.type)
            writeVarInt(packet.category.ordinal)
            val pos = packet.position
            writeInt((pos.x * 8.0).toInt())
            writeInt((pos.y * 8.0).toInt())
            writeInt((pos.z * 8.0).toInt())
            writeFloat(packet.volume)
            writeFloat(packet.pitch)
        }
    }
}
