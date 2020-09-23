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
package org.lanternpowered.server.network.value

import org.lanternpowered.server.network.buffer.ByteBuffer
import org.lanternpowered.server.network.packet.codec.CodecContext

interface ContextualValueWriter<V> {

    fun write(ctx: CodecContext, buf: ByteBuffer, value: V)

    fun writeAt(ctx: CodecContext, buf: ByteBuffer, index: Int, value: V) {
        val originalIndex = buf.writerIndex()
        try {
            buf.writerIndex(index)
            this.write(ctx, buf, value)
        } finally {
            buf.writerIndex(originalIndex)
        }
    }
}
