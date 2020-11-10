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
import org.lanternpowered.server.network.packet.PacketDecoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientUseEntityPacket
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientUseEntityPacket.Attack
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientUseEntityPacket.Interact
import org.spongepowered.api.data.type.HandTypes
import org.spongepowered.math.vector.Vector3d

object ClientUseEntityDecoder : PacketDecoder<ClientUseEntityPacket> {

    override fun decode(ctx: CodecContext, buf: ByteBuffer): ClientUseEntityPacket {
        val entityId = buf.readVarInt()
        val action = buf.readVarInt()
        return if (action == 1) {
            val isSneaking = buf.readBoolean()
            Attack(entityId, isSneaking)
        } else if (action == 0 || action == 2) {
            val position = if (action == 2) {
                val x = buf.readFloat().toDouble()
                val y = buf.readFloat().toDouble()
                val z = buf.readFloat().toDouble()
                Vector3d(x, y, z)
            } else null
            val hand = if (buf.readVarInt() == 0) HandTypes.MAIN_HAND.get() else HandTypes.OFF_HAND.get()
            val isSneaking = buf.readBoolean()
            Interact(entityId, hand, position, isSneaking)
        } else {
            throw DecoderException("Received a UseEntity message with a unknown action: $action")
        }
    }
}
