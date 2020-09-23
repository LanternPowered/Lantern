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
import org.lanternpowered.server.network.packet.codec.Codec
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnMobPacket
import kotlin.math.min

object SpawnMobEncoder : PacketEncoder<SpawnMobPacket> {

    override fun encode(ctx: CodecContext, packet: SpawnMobPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeVarInt(packet.entityId)
        buf.writeUniqueId(packet.uniqueId)
        buf.writeVarInt(packet.mobType)
        buf.writeVector3d(packet.position)
        buf.writeByte(packet.yaw)
        buf.writeByte(packet.pitch)
        buf.writeByte(packet.headPitch)
        val velocity = packet.velocity
        buf.writeShort(min(velocity.x * 8000.0, Short.MAX_VALUE.toDouble()).toShort())
        buf.writeShort(min(velocity.y * 8000.0, Short.MAX_VALUE.toDouble()).toShort())
        buf.writeShort(min(velocity.z * 8000.0, Short.MAX_VALUE.toDouble()).toShort())
        return buf
    }
}
