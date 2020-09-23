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

import org.lanternpowered.api.boss.BossBarFlag
import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.text.NetworkText
import org.lanternpowered.server.network.vanilla.packet.type.play.BossBarPacket

object BossBarEncoder : PacketEncoder<BossBarPacket> {

    override fun encode(ctx: CodecContext, packet: BossBarPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeUniqueId(packet.uniqueId)
        when (packet) {
            is BossBarPacket.Add -> {
                buf.writeVarInt(0)
                NetworkText.write(ctx, buf, packet.title)
                buf.writeFloat(packet.percent)
                buf.writeVarInt(packet.color.ordinal)
                buf.writeVarInt(packet.overlay.ordinal)
                buf.writeByte(packet.flags.toInt().toByte())
            }
            is BossBarPacket.Remove -> {
                buf.writeVarInt(1)
            }
            is BossBarPacket.UpdatePercent -> {
                buf.writeVarInt(2)
                buf.writeFloat(packet.percent)
            }
            is BossBarPacket.UpdateName -> {
                buf.writeVarInt(3)
                NetworkText.write(ctx, buf, packet.title)
            }
            is BossBarPacket.UpdateStyle -> {
                buf.writeVarInt(4)
                buf.writeVarInt(packet.color.ordinal)
                buf.writeVarInt(packet.overlay.ordinal)
            }
            is BossBarPacket.UpdateFlags -> {
                buf.writeVarInt(5)
                buf.writeByte(packet.flags.toInt().toByte())
            }
        }
        return buf
    }

    private fun Set<BossBarFlag>.toInt(): Int {
        var value = 0
        if (this.contains(BossBarFlag.DARKEN_SCREEN))
            value += 0x1
        if (this.contains(BossBarFlag.PLAY_BOSS_MUSIC))
            value += 0x2
        if (this.contains(BossBarFlag.CREATE_WORLD_FOG))
            value += 0x4
        return value
    }
}
