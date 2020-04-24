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
import org.lanternpowered.server.network.vanilla.message.type.play.SpawnPaintingMessage
import org.lanternpowered.server.registry.type.data.ArtTypeRegistry
import org.spongepowered.api.util.Direction

class SpawnPaintingCodec : Codec<SpawnPaintingMessage> {

    override fun encode(context: CodecContext, message: SpawnPaintingMessage): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            writeVarInt(message.entityId)
            writeUniqueId(message.uniqueId)
            writeVarInt(ArtTypeRegistry.getId(message.artType))
            writePosition(message.position)
            writeByte(message.direction.getId())
        }
    }

    private fun Direction.getId(): Byte = when (this) {
        Direction.EAST -> 3
        Direction.NORTH -> 2
        Direction.WEST -> 1
        else -> 0
    }
}
