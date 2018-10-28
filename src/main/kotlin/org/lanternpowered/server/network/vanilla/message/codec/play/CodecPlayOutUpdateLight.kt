/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
 */
package org.lanternpowered.server.network.vanilla.message.codec.play

import io.netty.buffer.Unpooled
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.buffer.ByteBufferAllocator
import org.lanternpowered.server.network.buffer.LanternByteBuffer
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
