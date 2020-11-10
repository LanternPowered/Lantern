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
import org.lanternpowered.server.network.packet.PacketEncoder
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.vanilla.packet.type.play.SetWindowItemsPacket

object SetWindowItemsEncoder : PacketEncoder<SetWindowItemsPacket> {

    override fun encode(ctx: CodecContext, packet: SetWindowItemsPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeByte(packet.windowId.toByte())
        val items = packet.items
        buf.writeShort(items.size.toShort())
        for (item in items)
            NetworkItemStack.write(ctx, buf, item)
        return buf
    }
}
