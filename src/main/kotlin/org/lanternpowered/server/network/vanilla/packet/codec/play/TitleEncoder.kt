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
import org.lanternpowered.server.network.text.NetworkText
import org.lanternpowered.server.network.vanilla.packet.type.play.TitlePacket
import org.lanternpowered.server.network.vanilla.packet.type.play.TitlePacket.SetActionbarTitle
import org.lanternpowered.server.network.vanilla.packet.type.play.TitlePacket.SetSubtitle
import org.lanternpowered.server.network.vanilla.packet.type.play.TitlePacket.SetTimes
import org.lanternpowered.server.network.vanilla.packet.type.play.TitlePacket.SetTitle

object TitleEncoder : PacketEncoder<TitlePacket> {

    private const val SET_TITLE = 0
    private const val SET_SUBTITLE = 1
    private const val SET_ACTIONBAR_TITLE = 2
    private const val SET_TIMES = 3
    private const val CLEAR = 4
    private const val RESET = 5

    override fun encode(ctx: CodecContext, packet: TitlePacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        when (packet) {
            TitlePacket.Clear -> buf.writeVarInt(CLEAR)
            TitlePacket.Reset -> buf.writeVarInt(RESET)
            is SetTitle -> {
                buf.writeVarInt(SET_TITLE)
                NetworkText.write(ctx, buf, packet.title)
            }
            is SetSubtitle -> {
                buf.writeVarInt(SET_SUBTITLE)
                NetworkText.write(ctx, buf, packet.title)
            }
            is SetActionbarTitle -> {
                buf.writeVarInt(SET_ACTIONBAR_TITLE)
                NetworkText.write(ctx, buf, packet.title)
            }
            is SetTimes -> {
                buf.writeVarInt(SET_TIMES)
                val (fadeIn, stay, fadeOut) = packet
                buf.writeInt(fadeIn)
                buf.writeInt(stay)
                buf.writeInt(fadeOut)
            }
        }
        return buf
    }
}
