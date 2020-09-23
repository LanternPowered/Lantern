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

import org.lanternpowered.server.data.persistence.MemoryDataContainer
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.ChunkPacket
import org.spongepowered.api.data.persistence.DataQuery

object ChunkInitOrUpdateEncoder : PacketEncoder<ChunkPacket> {

    private val X = DataQuery.of("x")
    private val Y = DataQuery.of("y")
    private val Z = DataQuery.of("z")

    override fun encode(ctx: CodecContext, packet: ChunkPacket): ByteBuffer {
        val sections = if (packet is ChunkPacket.Init) packet.sections else (packet as ChunkPacket.Update).sections

        val x = packet.x
        val z = packet.z
        val buf = ctx.byteBufAlloc().buffer()

        buf.writeInt(packet.x)
        buf.writeInt(packet.z)
        buf.writeBoolean(packet is ChunkPacket.Init)
        buf.writeBoolean(packet is ChunkPacket.Init || (packet as ChunkPacket.Update).retainLighting)

        var sectionBitmask = 0
        val dataBuf = ctx.byteBufAlloc().buffer()
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

            if (blockEntitiesBuf == null)
                blockEntitiesBuf = ctx.byteBufAlloc().buffer()

            for (blockEntityEntry in blockEntities.short2ObjectEntrySet()) {
                blockEntitiesCount++
                val index = blockEntityEntry.shortKey.toInt() and 0xffff
                val dataView = blockEntityEntry.value
                dataView[X] = x * 16 + (index and 0xf)
                dataView[Y] = i shl 4 or index shr 8
                dataView[Z] = z * 16 + (index shr 4 and 0xf)
                blockEntitiesBuf.writeDataView(dataView)
            }
        }
        buf.writeVarInt(sectionBitmask)
        buf.writeDataView(MemoryDataContainer())

        if (packet is ChunkPacket.Init) {
            val biomes = packet.biomes
            buf.ensureWritable(biomes.size * Int.SIZE_BYTES)
            for (value in biomes)
                buf.writeInt(value)
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
}
