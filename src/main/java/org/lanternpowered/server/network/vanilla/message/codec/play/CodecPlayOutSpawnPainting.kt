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
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSpawnPainting
import org.lanternpowered.server.registry.type.data.ArtTypeRegistry
import org.spongepowered.api.util.Direction

class CodecPlayOutSpawnPainting : Codec<MessagePlayOutSpawnPainting> {

    override fun encode(context: CodecContext, message: MessagePlayOutSpawnPainting): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            writeVarInt(message.entityId)
            writeUniqueId(message.uniqueId)
            writeVarInt(ArtTypeRegistry.getId(message.art))
            writePosition(message.x, message.y, message.z)
            writeByte(toId(message.direction))
        }
    }

    private fun toId(direction: Direction): Byte = when (direction) {
        Direction.EAST -> 3
        Direction.NORTH -> 2
        Direction.WEST -> 1
        else -> 0
    }
}
