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
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.StopSoundsPacket

object StopSoundsEncoder : PacketEncoder<StopSoundsPacket> {

    override fun encode(ctx: CodecContext, packet: StopSoundsPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        val category = packet.category
        val sound = packet.sound
        var flags = 0
        if (category != null)
            flags += 0x1
        if (sound != null)
            flags += 0x2
        buf.writeByte(flags.toByte())
        if (category != null)
            buf.writeVarInt(category.ordinal)
        if (sound != null)
            buf.writeNamespacedKey(sound)
        return buf
    }
}
