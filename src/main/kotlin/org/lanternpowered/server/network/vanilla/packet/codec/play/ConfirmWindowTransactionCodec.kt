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
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.ConfirmWindowTransactionPacket

object ConfirmWindowTransactionCodec : PacketEncoder<ConfirmWindowTransactionPacket>, PacketDecoder<ConfirmWindowTransactionPacket> {

    override fun encode(ctx: CodecContext, packet: ConfirmWindowTransactionPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer(4)
        buf.writeByte(packet.windowId.toByte())
        buf.writeShort(packet.transaction.toShort())
        buf.writeBoolean(packet.isAccepted)
        return buf
    }

    override fun decode(ctx: CodecContext, buf: ByteBuffer): ConfirmWindowTransactionPacket {
        val windowId = buf.readByte().toInt()
        val transaction = buf.readShort().toInt()
        val accepted = buf.readBoolean()
        return ConfirmWindowTransactionPacket(windowId, transaction, accepted)
    }
}
