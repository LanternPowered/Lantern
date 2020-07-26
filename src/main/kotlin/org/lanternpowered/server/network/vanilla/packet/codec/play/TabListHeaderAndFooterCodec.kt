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
import org.lanternpowered.server.network.vanilla.packet.type.play.TabListHeaderAndFooterPacket

object TabListHeaderAndFooterCodec : Codec<TabListHeaderAndFooterPacket> {

    // This is the only text type that can be empty on the client
    // for the result of #getFormattedText
    private const val emptyText = "{\"translate\":\"\"}"

    override fun encode(context: CodecContext, packet: TabListHeaderAndFooterPacket): ByteBuffer {
        val buf = context.byteBufAlloc().buffer()
        val header = packet.header
        val footer = packet.footer
        if (header != null) {
            context.write(buf, ContextualValueTypes.TEXT, header)
        } else {
            buf.writeString(emptyText)
        }
        if (footer != null) {
            context.write(buf, ContextualValueTypes.TEXT, footer)
        } else {
            buf.writeString(emptyText)
        }
        return buf
    }
}
