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
import org.lanternpowered.server.network.text.NetworkText
import org.lanternpowered.server.network.vanilla.packet.type.play.TabCompletePacket

object TabCompleteEncoder : PacketEncoder<TabCompletePacket> {

    override fun encode(ctx: CodecContext, packet: TabCompletePacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeVarInt(packet.id)
        buf.writeVarInt(packet.start)
        buf.writeVarInt(packet.length)
        val matches = packet.matches
        buf.writeVarInt(matches.size)
        for ((value, tooltip) in matches) {
            buf.writeString(value)
            buf.writeBoolean(tooltip != null)
            if (tooltip != null)
                NetworkText.write(ctx, buf, tooltip)
        }
        return buf
    }
}
