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
import org.lanternpowered.server.network.packet.CodecContext
import org.lanternpowered.server.network.text.NetworkText
import org.lanternpowered.server.network.vanilla.packet.type.play.OpenWindowPacket

object OpenWindowEncoder : PacketEncoder<OpenWindowPacket> {

    override fun encode(ctx: CodecContext, packet: OpenWindowPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        buf.writeVarInt(packet.windowId)
        buf.writeVarInt(packet.windowType.internalId)
        NetworkText.write(ctx, buf, packet.title)
        return buf
    }
}
