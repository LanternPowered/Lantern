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
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.MultiBlockChangePacket
import org.lanternpowered.server.world.chunk.LocalPositionHelper

object MultiBlockChangeEncoder : PacketEncoder<MultiBlockChangePacket> {

    override fun encode(ctx: CodecContext, packet: MultiBlockChangePacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        // The lantern chunk position is a section in vanilla mc, the packed
        // content also matches the content in vanilla, that's convenient
        buf.writeLong(packet.chunk.packed)
        buf.writeBoolean(packet.inverseTrustEdges)
        val changes = packet.changes
        buf.writeVarInt(changes.size)
        for ((position, blockState) in changes)
            buf.writeVarLong(position.packed.toLong() or (blockState.toLong() shl LocalPositionHelper.Bits))
        return buf
    }
}
