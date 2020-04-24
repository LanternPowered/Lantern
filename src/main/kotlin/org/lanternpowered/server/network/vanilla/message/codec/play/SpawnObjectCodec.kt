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
import org.lanternpowered.server.network.vanilla.message.type.play.SpawnObjectMessage

class SpawnObjectCodec : Codec<SpawnObjectMessage> {

    override fun encode(context: CodecContext, message: SpawnObjectMessage): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            writeVarInt(message.entityId)
            writeUniqueId(message.uniqueId)
            writeVarInt(message.objectType)
            writeVector3d(message.position)
            writeByte(message.pitch.toByte())
            writeByte(message.yaw.toByte())
            writeInt(message.objectData)
            val velocity = message.velocity
            writeShort((velocity.x * 8000.0).coerceAtMost(Short.MAX_VALUE.toDouble()).toShort())
            writeShort((velocity.y * 8000.0).coerceAtMost(Short.MAX_VALUE.toDouble()).toShort())
            writeShort((velocity.z * 8000.0).coerceAtMost(Short.MAX_VALUE.toDouble()).toShort())
        }
    }
}
