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
package org.lanternpowered.server.network.vanilla.message.codec.play

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnMob
import kotlin.math.min

class CodecPlayOutSpawnMob : Codec<MessagePlayOutSpawnMob> {

    override fun encode(context: CodecContext, message: MessagePlayOutSpawnMob): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            writeVarInt(message.entityId)
            writeUniqueId(message.uniqueId)
            writeVarInt(message.mobType)
            writeVector3d(message.position)
            writeByte(message.yaw)
            writeByte(message.pitch)
            writeByte(message.headPitch)
            val velocity = message.velocity
            writeShort(min(velocity.x * 8000.0, Short.MAX_VALUE.toDouble()).toShort())
            writeShort(min(velocity.y * 8000.0, Short.MAX_VALUE.toDouble()).toShort())
            writeShort(min(velocity.z * 8000.0, Short.MAX_VALUE.toDouble()).toShort())
        }
    }
}
