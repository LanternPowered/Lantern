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
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerPositionAndLookPacket
import org.spongepowered.api.util.RelativePositions

object PlayerPositionAndLookEncoder : PacketEncoder<PlayerPositionAndLookPacket> {

    override fun encode(ctx: CodecContext, packet: PlayerPositionAndLookPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeVector3d(packet.position)
        buf.writeFloat(packet.yaw)
        buf.writeFloat(packet.pitch)
        val relativePositions = packet.relativePositions
        var flags = 0
        if (relativePositions.contains(RelativePositions.X))
            flags += 0x01
        if (relativePositions.contains(RelativePositions.Y))
            flags += 0x02
        if (relativePositions.contains(RelativePositions.Z))
            flags += 0x04
        if (relativePositions.contains(RelativePositions.PITCH))
            flags += 0x08
        if (relativePositions.contains(RelativePositions.YAW))
            flags += 0x10
        buf.writeByte(flags.toByte())
        buf.writeVarInt(packet.teleportId)
        return buf
    }
}
