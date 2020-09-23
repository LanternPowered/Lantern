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
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientEditCommandBlockPacket

object ClientEditCommandBlockBlockCodec : PacketDecoder<ClientEditCommandBlockPacket.Block> {

    private val modes = ClientEditCommandBlockPacket.Block.Mode.values()

    override fun decode(ctx: CodecContext, buf: ByteBuffer): ClientEditCommandBlockPacket.Block {
        val pos = buf.readBlockPosition()
        val command = buf.readString()
        val mode = this.modes[buf.readVarInt()]
        val flags = buf.readByte().toInt()
        val shouldTrackOutput = flags and 0x1 != 0
        val conditional = flags and 0x2 != 0
        val automatic = flags and 0x4 != 0
        return ClientEditCommandBlockPacket.Block(pos, command, shouldTrackOutput, mode, conditional, automatic)
    }
}
