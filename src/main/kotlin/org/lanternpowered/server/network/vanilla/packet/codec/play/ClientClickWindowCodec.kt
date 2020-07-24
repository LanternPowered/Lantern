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
import org.lanternpowered.server.network.packet.codec.Codec
import org.lanternpowered.server.network.packet.codec.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientClickWindowPacket

object ClientClickWindowCodec : Codec<ClientClickWindowPacket> {

    override fun decode(context: CodecContext, buf: ByteBuffer): ClientClickWindowPacket {
        return buf.run {
            val windowId = readByte().toInt()
            val slot = readShort().toInt()
            val button = readByte().toInt()
            val transaction = readShort().toInt()
            val mode = readVarInt()
            val clickedItem = context.read(buf, ContextualValueTypes.ITEM_STACK)
            ClientClickWindowPacket(windowId, slot, mode, button, transaction, clickedItem)
        }
    }
}
