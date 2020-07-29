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
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.ScoreboardObjectivePacket
import org.lanternpowered.server.registry.type.scoreboard.ObjectiveDisplayModeRegistry

object ScoreboardObjectiveCodec : PacketEncoder<ScoreboardObjectivePacket> {

    override fun encode(context: CodecContext, packet: ScoreboardObjectivePacket): ByteBuffer {
        val buf = context.byteBufAlloc().buffer()
        buf.writeString(packet.objectiveName)
        if (packet is ScoreboardObjectivePacket.CreateOrUpdate) {
            buf.writeByte((if (packet is ScoreboardObjectivePacket.Create) 0 else 2).toByte())
            context.write(buf, ContextualValueTypes.TEXT, packet.displayName)
            buf.writeVarInt(ObjectiveDisplayModeRegistry.getId(packet.displayMode))
        } else {
            buf.writeByte(1.toByte())
        }
        return buf
    }
}
