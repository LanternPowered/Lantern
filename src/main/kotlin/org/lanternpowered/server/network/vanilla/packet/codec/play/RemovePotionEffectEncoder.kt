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
import org.lanternpowered.server.network.vanilla.packet.type.play.RemovePotionEffectPacket
import org.lanternpowered.server.registry.type.potion.PotionEffectTypeRegistry

object RemovePotionEffectEncoder : PacketEncoder<RemovePotionEffectPacket> {

    override fun encode(ctx: CodecContext, packet: RemovePotionEffectPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeVarInt(packet.entityId)
        buf.writeByte(PotionEffectTypeRegistry.getId(packet.type).toByte())
        return buf
    }
}
