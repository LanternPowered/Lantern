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
import org.lanternpowered.server.network.item.NetworkItemStack
import org.lanternpowered.server.network.packet.PacketDecoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.ClientClickWindowPacket

object ClientClickWindowDecoder : PacketDecoder<ClientClickWindowPacket> {

    override fun decode(ctx: CodecContext, buf: ByteBuffer): ClientClickWindowPacket {
        val windowId = buf.readByte().toInt()
        val slot = buf.readShort().toInt()
        val button = buf.readByte().toInt()
        val transaction = buf.readShort().toInt()
        val mode = buf.readVarInt()
        val clickedItem = NetworkItemStack.read(ctx, buf)
        return ClientClickWindowPacket(windowId, slot, mode, button, transaction, clickedItem)
    }
}
