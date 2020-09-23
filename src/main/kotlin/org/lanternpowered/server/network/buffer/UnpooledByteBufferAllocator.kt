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
package org.lanternpowered.server.network.buffer

import io.netty.buffer.Unpooled

object UnpooledByteBufferAllocator : ByteBufferAllocator {

    override fun buffer(): ByteBuffer = this.buffer()
    override fun buffer(initialCapacity: Int): ByteBuffer = this.buffer(initialCapacity)
    override fun heapBuffer(): ByteBuffer = LanternByteBuffer(Unpooled.buffer())
    override fun heapBuffer(initialCapacity: Int): ByteBuffer = LanternByteBuffer(Unpooled.buffer(initialCapacity))
    override fun directBuffer(): ByteBuffer = LanternByteBuffer(Unpooled.directBuffer())
    override fun directBuffer(initialCapacity: Int): ByteBuffer = LanternByteBuffer(Unpooled.directBuffer(initialCapacity))

    fun wrappedBuffer(byteArray: ByteArray): ByteBuffer = LanternByteBuffer(Unpooled.wrappedBuffer(byteArray))
}
