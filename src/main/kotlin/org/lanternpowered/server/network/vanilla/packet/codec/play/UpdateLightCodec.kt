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
import org.lanternpowered.server.network.buffer.ByteBufferAllocator
import org.lanternpowered.server.network.packet.codec.Codec
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.UpdateLightPacket

class UpdateLightCodec : Codec<UpdateLightPacket> {

    override fun encode(ctx: CodecContext, packet: UpdateLightPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeVarInt(packet.chunkX)
        buf.writeVarInt(packet.chunkZ)
        var mask = 0
        var emptyMask = 0
        var dataBlockCount = 0 // The amount of sky light blocks, each 2048 bytes
        fun computeLightMasks(array: Array<ByteArray?>) {
            array.filterNotNull().forEachIndexed { index, bytes ->
                if (bytes === UpdateLightPacket.EMPTY_SECTION) {
                    emptyMask = emptyMask or (1 shl index)
                } else {
                    mask = mask or (1 shl index)
                    dataBlockCount++
                }
            }
        }
        computeLightMasks(packet.skyLight)
        buf.writeVarInt(mask)
        val emptySkyMask = emptyMask
        mask = 0
        emptyMask = 0
        computeLightMasks(packet.blockLight)
        buf.writeVarInt(mask)
        buf.writeVarInt(emptySkyMask)
        buf.writeVarInt(emptyMask)
        buf.ensureWritable(UpdateLightPacket.SECTION_BYTES * (dataBlockCount + SECTION_LENGTH_BYTES))
        fun writeLight(array: Array<ByteArray?>) {
            array.filterNotNull()
                    .filter { it !== UpdateLightPacket.EMPTY_SECTION }
                    .forEach {
                        buf.writeVarInt(UpdateLightPacket.SECTION_BYTES)
                        buf.writeBytes(it)
                    }
        }
        writeLight(packet.skyLight)
        writeLight(packet.blockLight)
        return buf
    }

    companion object {

        private val SECTION_LENGTH_BYTES: Int

        init {
            // Determine the amount of bytes used for the
            // sky light length, it's currently a constant
            val buffer = ByteBufferAllocator.unpooled().buffer()
            try {
                SECTION_LENGTH_BYTES = buffer.writeVarInt(UpdateLightPacket.SECTION_BYTES).writerIndex()
            } finally {
                buffer.release()
            }
        }
    }
}
