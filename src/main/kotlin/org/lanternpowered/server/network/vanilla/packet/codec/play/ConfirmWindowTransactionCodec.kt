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
import org.lanternpowered.server.network.packet.codec.Codec
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.ConfirmWindowTransactionPacket

object ConfirmWindowTransactionCodec : Codec<ConfirmWindowTransactionPacket> {

    override fun encode(context: CodecContext, packet: ConfirmWindowTransactionPacket): ByteBuffer {
        val buf = context.byteBufAlloc().buffer(4)
        buf.writeByte(packet.windowId.toByte())
        buf.writeShort(packet.transaction.toShort())
        buf.writeBoolean(packet.isAccepted)
        return buf
    }

    override fun decode(context: CodecContext, buf: ByteBuffer): ConfirmWindowTransactionPacket {
        val windowId = buf.readByte().toInt()
        val transaction = buf.readShort().toInt()
        val accepted = buf.readBoolean()
        return ConfirmWindowTransactionPacket(windowId, transaction, accepted)
    }
}
