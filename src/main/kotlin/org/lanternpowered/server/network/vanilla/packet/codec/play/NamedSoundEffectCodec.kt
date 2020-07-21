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
import org.lanternpowered.server.network.packet.codec.Codec
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.NamedSoundEffectPacket

class NamedSoundEffectCodec : Codec<NamedSoundEffectPacket> {

    override fun encode(context: CodecContext, packet: NamedSoundEffectPacket): ByteBuffer {
        val buf = context.byteBufAlloc().buffer()
        buf.writeString(packet.type)
        buf.writeVarInt(packet.category.ordinal)
        val pos = packet.position
        buf.writeInt((pos.x * 8.0).toInt())
        buf.writeInt((pos.y * 8.0).toInt())
        buf.writeInt((pos.z * 8.0).toInt())
        buf.writeFloat(packet.volume)
        buf.writeFloat(packet.pitch)
        return buf
    }
}
