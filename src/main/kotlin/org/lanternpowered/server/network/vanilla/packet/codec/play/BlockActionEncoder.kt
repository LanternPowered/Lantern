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
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.BlockActionPacket

object BlockActionEncoder : PacketEncoder<BlockActionPacket> {

    override fun encode(ctx: CodecContext, packet: BlockActionPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeBlockPosition(packet.position)
        val parameters = packet.parameters
        buf.writeByte(parameters[0].toByte())
        buf.writeByte(parameters[1].toByte())
        buf.writeVarInt(packet.blockType)
        return buf
    }
}
