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

import io.netty.handler.codec.CodecException
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.Packet
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.FinishUsingItemPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.EntityStatusPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SetOpLevelPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.SetReducedDebugPacket

object EntityStatusEncoder : PacketEncoder<Packet> {

    private const val LENGTH = Int.SIZE_BYTES + Byte.SIZE_BYTES

    override fun encode(ctx: CodecContext, packet: Packet): ByteBuffer {
        val entityId: Int
        val action: Int
        when (packet) {
            is SetReducedDebugPacket -> {
                entityId = ctx.channel.attr(PlayerJoinEncoder.PLAYER_ENTITY_ID).get()
                action = if (packet.isReduced) 22 else 23
            }
            is SetOpLevelPacket -> {
                entityId = ctx.channel.attr(PlayerJoinEncoder.PLAYER_ENTITY_ID).get()
                action = 24 + packet.opLevel.coerceIn(0..4)
            }
            is EntityStatusPacket -> {
                entityId = packet.entityId
                action = packet.status
            }
            is FinishUsingItemPacket -> {
                entityId = ctx.channel.attr(PlayerJoinEncoder.PLAYER_ENTITY_ID).get()
                action = 9
            }
            else -> throw CodecException("Unsupported message type: " + packet.javaClass.name)
        }
        return ctx.byteBufAlloc().buffer(LENGTH).writeInt(entityId).writeByte(action.toByte())
    }
}
