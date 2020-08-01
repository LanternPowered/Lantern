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
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.StatisticsPacket

object StatisticsCodec : PacketEncoder<StatisticsPacket> {

    override fun encode(context: CodecContext, packet: StatisticsPacket): ByteBuffer {
        val buf = context.byteBufAlloc().buffer()
        val entries = packet.entries
        buf.writeVarInt(entries.size)
        for ((name, value) in entries) {
            buf.writeString(name)
            buf.writeVarInt(value)
        }
        return buf
    }
}