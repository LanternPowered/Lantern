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
import org.lanternpowered.server.network.item.NetworkItemStack
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.SpawnParticlePacket

object SpawnParticleEncoder : PacketEncoder<SpawnParticlePacket> {

    private const val baseLength = Int.SIZE_BYTES * 2 + Byte.SIZE_BYTES + Int.SIZE_BYTES /* float */ * 7

    override fun encode(ctx: CodecContext, packet: SpawnParticlePacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer(this.baseLength)
        buf.writeInt(packet.particleId)
        buf.writeBoolean(packet.isLongDistance)
        buf.writeVector3d(packet.position)
        buf.writeVector3f(packet.offset)
        buf.writeFloat(packet.data)
        buf.writeInt(packet.quantity)
        val extra = packet.extra
        if (extra != null) {
            when (extra) {
                is SpawnParticlePacket.ItemData -> NetworkItemStack.write(ctx, buf, extra.itemStack)
                is SpawnParticlePacket.BlockData -> buf.writeVarInt(extra.blockState)
                is SpawnParticlePacket.DustData -> {
                    buf.writeFloat(extra.red)
                    buf.writeFloat(extra.green)
                    buf.writeFloat(extra.blue)
                    buf.writeFloat(extra.scale)
                }
                else -> throw EncoderException("Unsupported extra data type: " + extra.javaClass.name)
            }
        }
        return buf
    }
}
