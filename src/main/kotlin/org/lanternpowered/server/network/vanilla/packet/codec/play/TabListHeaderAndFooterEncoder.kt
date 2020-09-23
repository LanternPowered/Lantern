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
import org.lanternpowered.server.network.vanilla.packet.type.play.TabListHeaderAndFooterPacket

object TabListHeaderAndFooterEncoder : PacketEncoder<TabListHeaderAndFooterPacket> {

    // This is the only text type that can be empty on the client
    // for the result of #getFormattedText
    private const val emptyText = "{\"text\":\"\"}"

    override fun encode(ctx: CodecContext, packet: TabListHeaderAndFooterPacket): ByteBuffer {
        val buf = ctx.byteBufAlloc().buffer()
        val header = packet.header
        val footer = packet.footer
        if (header != null) {
            NetworkText.write(ctx, buf, header)
        } else {
            buf.writeString(emptyText)
        }
        if (footer != null) {
            NetworkText.write(ctx, buf, footer)
        } else {
            buf.writeString(emptyText)
        }
        return buf
    }
}
