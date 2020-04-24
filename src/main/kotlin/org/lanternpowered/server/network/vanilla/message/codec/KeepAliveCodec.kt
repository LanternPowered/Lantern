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
package org.lanternpowered.server.network.vanilla.message.codec

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.KeepAliveMessage

class KeepAliveCodec : Codec<KeepAliveMessage> {

    override fun encode(context: CodecContext, message: KeepAliveMessage): ByteBuffer =
            context.byteBufAlloc().buffer(Long.SIZE_BYTES).writeLong(message.time)

    override fun decode(context: CodecContext, buf: ByteBuffer): KeepAliveMessage = KeepAliveMessage(buf.readLong())
}
