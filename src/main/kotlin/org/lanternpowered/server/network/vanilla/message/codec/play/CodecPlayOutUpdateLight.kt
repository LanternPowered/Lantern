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
import org.lanternpowered.server.network.buffer.ByteBufferAllocator
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutUpdateLight

class CodecPlayOutUpdateLight : Codec<MessagePlayOutUpdateLight> {

    override fun encode(ctx: CodecContext, message: MessagePlayOutUpdateLight): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeVarInt(message.chunkX)
        buf.writeVarInt(message.chunkZ)
        var mask = 0
        var emptyMask = 0
        var dataBlockCount = 0 // The amount of sky light blocks, each 2048 bytes
        fun computeLightMasks(array: Array<ByteArray?>) {
            array.filterNotNull().forEachIndexed { index, bytes ->
                if (bytes === MessagePlayOutUpdateLight.EMPTY_SECTION) {
                    emptyMask = emptyMask or (1 shl index)
                } else {
                    mask = mask or (1 shl index)
                    dataBlockCount++
                }
            }
        }
        computeLightMasks(message.skyLight)
        buf.writeVarInt(mask)
        val emptySkyMask = emptyMask
        mask = 0
        emptyMask = 0
        computeLightMasks(message.blockLight)
        buf.writeVarInt(mask)
        buf.writeVarInt(emptySkyMask)
        buf.writeVarInt(emptyMask)
        buf.ensureWritable(MessagePlayOutUpdateLight.SECTION_BYTES * (dataBlockCount + SECTION_LENGTH_BYTES))
        fun writeLight(array: Array<ByteArray?>) {
            array.filterNotNull()
                    .filter { it !== MessagePlayOutUpdateLight.EMPTY_SECTION }
                    .forEach {
                        buf.writeVarInt(MessagePlayOutUpdateLight.SECTION_BYTES)
                        buf.writeBytes(it)
                    }
        }
        writeLight(message.skyLight)
        writeLight(message.blockLight)
        return buf
    }

    companion object {

        private val SECTION_LENGTH_BYTES: Int

        init {
            // Determine the amount of bytes used for the
            // sky light length, it's currently a constant
            val buffer = ByteBufferAllocator.unpooled().buffer()
            try {
                SECTION_LENGTH_BYTES = buffer.writeVarInt(MessagePlayOutUpdateLight.SECTION_BYTES).writerIndex()
            } finally {
                buffer.release()
            }
        }
    }
}
