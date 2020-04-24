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
import org.lanternpowered.server.network.message.codec.Codec
import org.lanternpowered.server.network.message.codec.CodecContext
import org.lanternpowered.server.network.vanilla.message.type.play.CloseWindowMessage

class CloseWindowCodec : Codec<CloseWindowMessage> {

    override fun encode(context: CodecContext, message: CloseWindowMessage): ByteBuffer =
            context.byteBufAlloc().buffer(1).writeByte(message.window.toByte())

    override fun decode(context: CodecContext, buf: ByteBuffer): CloseWindowMessage = CloseWindowMessage(buf.readByte().toInt())
}
