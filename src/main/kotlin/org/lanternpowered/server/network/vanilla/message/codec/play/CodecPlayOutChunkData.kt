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

import org.lanternpowered.server.data.persistence.MemoryDataContainer
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutChunkData
import org.spongepowered.api.data.persistence.DataQuery

class CodecPlayOutChunkData : Codec<MessagePlayOutChunkData> {

    override fun encode(context: CodecContext, message: MessagePlayOutChunkData): ByteBuffer {
        val sections = message.sections

        val x = message.x
        val z = message.z
        val buf = context.byteBufAlloc().buffer()

        buf.writeInt(message.x)
        buf.writeInt(message.z)
        buf.writeBoolean(message is MessagePlayOutChunkData.Init)

        var sectionBitmask = 0
        val dataBuf = context.byteBufAlloc().buffer()
        var blockEntitiesBuf: ByteBuffer? = null
        var blockEntitiesCount = 0
        for (i in sections.indices) {
            val section = sections[i] ?: continue
            sectionBitmask = sectionBitmask or (1 shl i)
            val types = section.types
            dataBuf.writeShort(section.nonAirBlockCount.toShort())
            dataBuf.writeByte(types.bitsPerValue.toByte())
            val palette = section.palette
            if (palette != null) {
                dataBuf.writeVarInt(palette.size)
                for (value in palette) {
                    dataBuf.writeVarInt(value)
                }
            }
            val backing = types.backing
            dataBuf.writeVarInt(backing.size)
            dataBuf.ensureWritable(backing.size * Long.SIZE_BYTES)
            for (value in backing) {
                dataBuf.writeLong(value)
            }

            val blockEntities = section.blockEntities
            if (blockEntities.isEmpty())
                continue

            if (blockEntitiesBuf == null) {
                blockEntitiesBuf = context.byteBufAlloc().buffer()
            }

            for (blockEntityEntry in blockEntities.short2ObjectEntrySet()) {
                blockEntitiesCount++
                val index = blockEntityEntry.shortKey.toInt() and 0xffff
                val dataView = blockEntityEntry.value
                dataView[X] = x * 16 + (index and 0xf)
                dataView[Y] = i shl 4 or index shr 8
                dataView[Z] = z * 16 + (index shr 4 and 0xf)
                blockEntitiesBuf!!.writeDataView(dataView)
            }
        }
        buf.writeVarInt(sectionBitmask)
        buf.writeDataView(MemoryDataContainer())

        if (message is MessagePlayOutChunkData.Init) {
            val biomes = message.biomes
            buf.ensureWritable(biomes.size * Int.SIZE_BYTES)
            for (value in biomes) {
                buf.writeInt(value)
            }
        }

        buf.writeVarInt(dataBuf.writerIndex())
        try {
            buf.writeBytes(dataBuf)
        } finally {
            dataBuf.release()
        }

        buf.writeVarInt(blockEntitiesCount)
        if (blockEntitiesBuf != null) {
            try {
                buf.writeBytes(blockEntitiesBuf)
            } finally {
                blockEntitiesBuf.release()
            }
        }
        return buf
    }

    companion object {
        private val X = DataQuery.of("x")
        private val Y = DataQuery.of("y")
        private val Z = DataQuery.of("z")
    }
}
