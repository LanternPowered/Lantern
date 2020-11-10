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

import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.UpdateBlockEntityPacket
import org.spongepowered.api.data.persistence.DataQuery

object UpdateBlockEntityEncoder : PacketEncoder<UpdateBlockEntityPacket> {

    private val idQuery = DataQuery.of("id")
    private val xQuery = DataQuery.of("x")
    private val yQuery = DataQuery.of("y")
    private val zQuery = DataQuery.of("z")

    // The inbuilt block entity types, to send updates
    private val hardcodedTypes = listOf(
            "minecraft:mob_spawner",
            "minecraft:command_block",
            "minecraft:beacon",
            "minecraft:skull",
            "minecraft:conduit",
            "minecraft:banner",
            "minecraft:structure_block",
            "minecraft:end_gateway",
            "minecraft:sign",
            "",  // Unused
            "minecraft:bed",
            "minecraft:jigsaw",
            "minecraft:campfire",
            "minecraft:beehive"
    ).mapIndexed { index, key -> key to index }
            .toMap(Object2IntOpenHashMap())
            .also { it.defaultReturnValue(-1) }

    override fun encode(ctx: CodecContext, packet: UpdateBlockEntityPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        val pos = packet.position
        buf.writeBlockPosition(pos)
        val id = packet.type
        buf.writeByte((this.hardcodedTypes.getInt(id) + 1).toByte())
        val dataView = packet.data
        dataView[this.idQuery] = id
        dataView[this.xQuery] = pos.x
        dataView[this.yQuery] = pos.y
        dataView[this.zQuery] = pos.z
        buf.writeDataView(dataView)
        return buf
    }
}
