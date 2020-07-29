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

import com.google.common.collect.ImmutableMap
import it.unimi.dsi.fastutil.ints.IntArrayList
import it.unimi.dsi.fastutil.ints.IntList
import org.lanternpowered.server.game.registry.InternalIDRegistries
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.TagsPacket

object TagsCodec : PacketEncoder<TagsPacket> {

    override fun encode(context: CodecContext, packet: TagsPacket): ByteBuffer {
        val buf = context.byteBufAlloc().buffer()
        // TODO: Replace this hack, is currently required to
        // TODO: avoid crashes on the client.
        val wallSigns: IntList = IntArrayList()
        val standingSigns: IntList = IntArrayList()
        for (entry in InternalIDRegistries.BLOCK_TYPE_IDS.object2IntEntrySet()) {
            if (entry.key.endsWith("_wall_sign")) {
                wallSigns.add(entry.intValue)
            } else if (entry.key.endsWith("_sign")) {
                standingSigns.add(entry.intValue)
            }
        }
        val signs: IntList = IntArrayList(wallSigns)
        signs.addAll(standingSigns)
        writeTags(buf, ImmutableMap.builder<String, IntList>()
                .put("minecraft:signs", signs)
                .put("minecraft:wall_signs", wallSigns)
                .put("minecraft:standing_signs", standingSigns)
                .build()) // Block Tags
        writeTags(buf, emptyMap()) // Item Tags
        writeTags(buf, emptyMap()) // Fluid Tags
        writeTags(buf, emptyMap()) // Entity Tags
        return buf
    }

    private fun writeTags(buf: ByteBuffer, entries: Map<String, IntList>) {
        buf.writeVarInt(entries.size)
        for ((key, ids) in entries) {
            buf.writeString(key)
            buf.writeVarInt(ids.size)
            for (id in ids.iterator())
                buf.writeVarInt(id)
        }
    }
}
