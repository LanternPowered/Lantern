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

import org.lanternpowered.api.util.math.component1
import org.lanternpowered.api.util.math.component2
import org.lanternpowered.api.util.math.component3
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.NamedSoundEffectPacket

object NamedSoundEffectEncoder : PacketEncoder<NamedSoundEffectPacket> {

    override fun encode(ctx: CodecContext, packet: NamedSoundEffectPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeNamespacedKey(packet.key)
        buf.writeVarInt(packet.category.ordinal)
        val (x, y, z) = packet.position
        buf.writeInt((x * 8.0).toInt())
        buf.writeInt((y * 8.0).toInt())
        buf.writeInt((z * 8.0).toInt())
        buf.writeFloat(packet.volume)
        buf.writeFloat(packet.pitch)
        return buf
    }
}
