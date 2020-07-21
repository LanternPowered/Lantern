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
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.BossBarPacket

class BossBarCodec : Codec<BossBarPacket> {

    override fun encode(context: CodecContext, packet: BossBarPacket): ByteBuffer {
        return context.byteBufAlloc().buffer().apply {
            writeUniqueId(packet.uniqueId)
            when (packet) {
                is BossBarPacket.Add -> {
                    writeVarInt(0)
                    context.write(this, ContextualValueTypes.TEXT, packet.title)
                    writeFloat(packet.percent)
                    writeVarInt(packet.color.ordinal)
                    writeVarInt(packet.overlay.ordinal)
                    writeByte(packet.flags.toInt().toByte())
                }
                is BossBarPacket.Remove -> {
                    writeVarInt(1)
                }
                is BossBarPacket.UpdatePercent -> {
                    writeVarInt(2)
                    writeFloat(packet.percent)
                }
                is BossBarPacket.UpdateName -> {
                    writeVarInt(3)
                    context.write(this, ContextualValueTypes.TEXT, packet.title)
                }
                is BossBarPacket.UpdateStyle -> {
                    writeVarInt(4)
                    writeVarInt(packet.color.ordinal)
                    writeVarInt(packet.overlay.ordinal)
                }
                is BossBarPacket.UpdateFlags -> {
                    writeVarInt(5)
                    writeByte(packet.flags.toInt().toByte())
                }
            }
        }
    }

    private fun Set<BossBarFlag>.toInt(): Int {
        var value = 0
        if (contains(BossBarFlag.DARKEN_SCREEN))
            value += 0x1
        if (contains(BossBarFlag.PLAY_BOSS_MUSIC))
            value += 0x2
        if (contains(BossBarFlag.CREATE_WORLD_FOG))
            value += 0x4
        return value
    }
}
