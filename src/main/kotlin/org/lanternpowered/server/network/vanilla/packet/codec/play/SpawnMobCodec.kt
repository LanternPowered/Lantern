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
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnMobPacket
import kotlin.math.min

class SpawnMobCodec : Codec<SpawnMobPacket> {

    override fun encode(context: CodecContext, packet: SpawnMobPacket): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            writeVarInt(packet.entityId)
            writeUniqueId(packet.uniqueId)
            writeVarInt(packet.mobType)
            writeVector3d(packet.position)
            writeByte(packet.yaw)
            writeByte(packet.pitch)
            writeByte(packet.headPitch)
            val velocity = packet.velocity
            writeShort(min(velocity.x * 8000.0, Short.MAX_VALUE.toDouble()).toShort())
            writeShort(min(velocity.y * 8000.0, Short.MAX_VALUE.toDouble()).toShort())
            writeShort(min(velocity.z * 8000.0, Short.MAX_VALUE.toDouble()).toShort())
        }
    }
}
