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
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.codec.Codec
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.SetWindowSlotPacket

object SetWindowSlotCodec : PacketEncoder<SetWindowSlotPacket> {

    override fun encode(context: CodecContext, packet: SetWindowSlotPacket): ByteBuffer {
        val buf = context.byteBufAlloc().buffer()
        buf.writeByte(packet.window.toByte())
        buf.writeShort(packet.index.toShort())
        val item = packet.item
        context.write(buf, ContextualValueTypes.ITEM_STACK, item)
        return buf
    }
}
