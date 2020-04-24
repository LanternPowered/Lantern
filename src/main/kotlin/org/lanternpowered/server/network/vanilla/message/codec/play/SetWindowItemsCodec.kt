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
package org.lanternpowered.server.network.vanilla.message.codec.play

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.buffer.contextual.ContextualValueTypes
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.play.SetWindowItemsMessage

class SetWindowItemsCodec : Codec<SetWindowItemsMessage> {

    override fun encode(context: CodecContext, message: SetWindowItemsMessage): ByteBuffer {
        val buf = context.byteBufAlloc().buffer()
        buf.writeByte(message.windowId.toByte())
        val items = message.items
        buf.writeShort(items.size.toShort())
        for (item in items) {
            context.write(buf, ContextualValueTypes.ITEM_STACK, item)
        }
        return buf
    }
}
