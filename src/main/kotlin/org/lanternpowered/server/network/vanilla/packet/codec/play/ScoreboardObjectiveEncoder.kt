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
import org.lanternpowered.server.network.vanilla.packet.type.play.ScoreboardObjectivePacket
import org.lanternpowered.server.registry.type.scoreboard.ObjectiveDisplayModeRegistry

object ScoreboardObjectiveEncoder : PacketEncoder<ScoreboardObjectivePacket> {

    override fun encode(ctx: CodecContext, packet: ScoreboardObjectivePacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeString(packet.objectiveName)
        if (packet is ScoreboardObjectivePacket.CreateOrUpdate) {
            buf.writeByte((if (packet is ScoreboardObjectivePacket.Create) 0 else 2).toByte())
            NetworkText.write(ctx, buf, packet.displayName)
            buf.writeVarInt(ObjectiveDisplayModeRegistry.getId(packet.displayMode))
        } else {
            buf.writeByte(1.toByte())
        }
        return buf
    }
}
