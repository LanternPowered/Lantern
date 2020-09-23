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
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerHealthPacket

object PlayerHealthEncoder : PacketEncoder<PlayerHealthPacket> {

    override fun encode(ctx: CodecContext, packet: PlayerHealthPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeFloat(packet.health)
        buf.writeVarInt(packet.food.toInt())
        buf.writeFloat(packet.saturation)
        return buf
    }
}
