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
import org.lanternpowered.server.network.text.LegacyNetworkText
import org.lanternpowered.server.network.vanilla.packet.type.play.ScoreboardScorePacket

object ScoreboardScoreEncoder : PacketEncoder<ScoreboardScorePacket> {

    override fun encode(ctx: CodecContext, packet: ScoreboardScorePacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        LegacyNetworkText.write(ctx, buf, packet.scoreName)
        buf.writeByte((if (packet is ScoreboardScorePacket.Remove) 1 else 0))
        buf.writeString(packet.objectiveName)
        if (packet is ScoreboardScorePacket.CreateOrUpdate)
            buf.writeVarInt(packet.value)
        return buf
    }
}
