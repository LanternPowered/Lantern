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
import org.lanternpowered.server.network.vanilla.packet.type.play.PlayerAbilitiesPacket

object PlayerAbilitiesCodec : PacketEncoder<PlayerAbilitiesPacket> {

    override fun encode(ctx: CodecContext, packet: PlayerAbilitiesPacket): ByteBuffer {
        var bits = 0
        if (packet.isInvulnerable)
            bits += 0x1
        if (packet.isFlying)
            bits += 0x2
        if (packet.canFly)
            bits += 0x4
        if (packet.isCreative)
            bits += 0x8
        val buf = ctx.byteBufAlloc().buffer(9)
        buf.writeByte(bits.toByte())
        buf.writeFloat(packet.flySpeed)
        buf.writeFloat(packet.fieldOfView)
        return buf
    }
}
