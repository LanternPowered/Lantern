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

interface ContextualValueReader<V> {

    fun read(ctx: CodecContext, buf: ByteBuffer): V

    fun readAt(ctx: CodecContext, buf: ByteBuffer, index: Int): V {
        val originalIndex = buf.readerIndex()
        try {
            buf.readerIndex(index)
            return this.read(ctx, buf)
        } finally {
            buf.readerIndex(originalIndex)
        }
    }
}
