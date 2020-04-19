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
package org.lanternpowered.server.network.vanilla.message.codec.connection

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageInOutKeepAlive

class CodecInOutKeepAlive : Codec<MessageInOutKeepAlive> {

    override fun encode(context: CodecContext, message: MessageInOutKeepAlive): ByteBuffer {
        return context.byteBufAlloc().buffer(Long.SIZE_BYTES).writeLong(message.time)
    }

    override fun decode(context: CodecContext, buf: ByteBuffer): MessageInOutKeepAlive {
        return MessageInOutKeepAlive(buf.readLong())
    }
}
