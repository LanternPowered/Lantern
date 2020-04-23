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

import io.netty.handler.codec.EncoderException
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.play.SpawnParticleMessage

class SpawnParticleCodec : Codec<SpawnParticleMessage> {

    override fun encode(context: CodecContext, message: SpawnParticleMessage): ByteBuffer {
        return context.byteBufAlloc().buffer(baseLength).apply {
            writeInt(message.particleId)
            writeBoolean(message.isLongDistance)
            writeVector3d(message.position)
            writeVector3f(message.offset)
            writeFloat(message.data)
            writeInt(message.quantity)
            val extra = message.extra
            if (extra != null) {
                when (extra) {
                    is SpawnParticleMessage.ItemData -> context.write(this, ContextualValueTypes.ITEM_STACK, extra.itemStack)
                    is SpawnParticleMessage.BlockData -> writeVarInt(extra.blockState)
                    is SpawnParticleMessage.DustData -> {
                        writeFloat(extra.red)
                        writeFloat(extra.green)
                        writeFloat(extra.blue)
                        writeFloat(extra.scale)
                    }
                    else -> throw EncoderException("Unsupported extra data type: " + extra.javaClass.name)
                }
            }
        }
    }

    companion object {

        private const val baseLength = Int.SIZE_BYTES * 2 + Byte.SIZE_BYTES + Int.SIZE_BYTES /* float */ * 7
    }
}
