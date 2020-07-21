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

import io.netty.handler.codec.EncoderException
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnParticlePacket

class SpawnParticleCodec : Codec<SpawnParticlePacket> {

    override fun encode(context: CodecContext, packet: SpawnParticlePacket): ByteBuffer {
        return context.byteBufAlloc().buffer(baseLength).apply {
            writeInt(packet.particleId)
            writeBoolean(packet.isLongDistance)
            writeVector3d(packet.position)
            writeVector3f(packet.offset)
            writeFloat(packet.data)
            writeInt(packet.quantity)
            val extra = packet.extra
            if (extra != null) {
                when (extra) {
                    is SpawnParticlePacket.ItemData -> context.write(this, ContextualValueTypes.ITEM_STACK, extra.itemStack)
                    is SpawnParticlePacket.BlockData -> writeVarInt(extra.blockState)
                    is SpawnParticlePacket.DustData -> {
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
