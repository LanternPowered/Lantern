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
import org.lanternpowered.server.network.packet.PacketDecoder
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.codec.play.CodecUtils.decodeDirection
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientBlockPlacementPacket
import org.spongepowered.api.data.type.HandTypes
import org.spongepowered.math.vector.Vector3d

object ClientBlockPlacementCodec : PacketDecoder<ClientBlockPlacementPacket> {

    override fun decode(ctx: CodecContext, buf: ByteBuffer): ClientBlockPlacementPacket {
        val hand = if (buf.readVarInt() == 0) HandTypes.MAIN_HAND.get() else HandTypes.OFF_HAND.get()
        val position = buf.readBlockPosition()
        val face = decodeDirection(buf.readVarInt())
        val ox = buf.readFloat().toDouble()
        val oy = buf.readFloat().toDouble()
        val oz = buf.readFloat().toDouble()
        val offset = Vector3d(ox, oy, oz)
        val insideBlock = buf.readBoolean()
        return ClientBlockPlacementPacket(position, offset, face, hand, insideBlock)
    }
}
