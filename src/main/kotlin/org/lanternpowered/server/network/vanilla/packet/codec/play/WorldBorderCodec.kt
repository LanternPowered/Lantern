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
import org.lanternpowered.server.network.vanilla.packet.type.play.WorldBorderPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.WorldBorderPacket.UpdateCenter
import org.lanternpowered.server.network.vanilla.packet.type.play.WorldBorderPacket.UpdateDiameter
import org.lanternpowered.server.network.vanilla.packet.type.play.WorldBorderPacket.UpdateLerpedDiameter
import org.lanternpowered.server.network.vanilla.packet.type.play.WorldBorderPacket.UpdateWarningDistance
import org.lanternpowered.server.network.vanilla.packet.type.play.WorldBorderPacket.UpdateWarningTime

object WorldBorderCodec : PacketEncoder<WorldBorderPacket> {

    override fun encode(context: CodecContext, packet: WorldBorderPacket): ByteBuffer {
        val buf = context.byteBufAlloc().buffer()
        when (packet) {
            is WorldBorderPacket.Initialize -> {
                val (centerX, centerZ, oldDiameter, newDiameter,
                        lerpTime, worldSize, warningDistance, warningTime) = packet
                buf.writeVarInt(3)
                buf.writeDouble(centerX)
                buf.writeDouble(centerZ)
                buf.writeDouble(oldDiameter)
                buf.writeDouble(newDiameter)
                buf.writeVarLong(lerpTime)
                buf.writeVarInt(worldSize)
                buf.writeVarInt(warningTime)
                buf.writeVarInt(warningDistance)
            }
            is UpdateCenter -> {
                val (x, z) = packet
                buf.writeVarInt(2)
                buf.writeDouble(x)
                buf.writeDouble(z)
            }
            is UpdateLerpedDiameter -> {
                val (oldDiameter, newDiameter, lerpTime) = packet
                buf.writeVarInt(1)
                buf.writeDouble(oldDiameter)
                buf.writeDouble(newDiameter)
                buf.writeVarLong(lerpTime)
            }
            is UpdateDiameter -> {
                buf.writeVarInt(0)
                buf.writeDouble(packet.diameter)
            }
            is UpdateWarningDistance -> {
                buf.writeVarInt(5)
                buf.writeVarInt(packet.distance)
            }
            is UpdateWarningTime -> {
                buf.writeVarInt(4)
                buf.writeVarInt(packet.time)
            }
        }
        return buf
    }
}
