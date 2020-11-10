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

import io.netty.handler.codec.DecoderException
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.packet.PacketDecoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientDropHeldItemPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.FinishUsingItemPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientDiggingPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientSwapHandItemsPacket

object ClientDiggingDecoder : PacketDecoder<Packet> {

    private val diggingActions = ClientDiggingPacket.Action.values()

    override fun decode(ctx: CodecContext, buf: ByteBuffer): Packet {
        val action = buf.readByte().toInt()
        val position = buf.readBlockPosition()
        val face = buf.readByte().toInt()
        return when (action) {
            0, 1, 2 -> ClientDiggingPacket(this.diggingActions[action], position, CodecUtils.decodeDirection(face))
            3, 4 -> ClientDropHeldItemPacket(action == 3)
            5 -> FinishUsingItemPacket
            6 -> ClientSwapHandItemsPacket
            else -> throw DecoderException("Unknown player digging message action: $action")
        }
    }
}
