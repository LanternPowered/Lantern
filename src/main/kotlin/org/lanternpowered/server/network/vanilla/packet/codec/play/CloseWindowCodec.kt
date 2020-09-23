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
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.CloseWindowPacket

object CloseWindowCodec : PacketEncoder<CloseWindowPacket>, PacketDecoder<CloseWindowPacket> {

    override fun encode(ctx: CodecContext, packet: CloseWindowPacket): ByteBuffer =
            ctx.byteBufAlloc().buffer(1).writeByte(packet.window.toByte())

    override fun decode(ctx: CodecContext, buf: ByteBuffer): CloseWindowPacket = CloseWindowPacket(buf.readByte().toInt())
}
