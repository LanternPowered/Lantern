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

import io.netty.buffer.ByteBufAllocator
import io.netty.buffer.PooledByteBufAllocator

class LanternByteBufferAllocator(private val allocator: ByteBufAllocator) : ByteBufferAllocator {

    override fun buffer(): ByteBuffer = LanternByteBuffer(this.allocator.buffer())
    override fun buffer(initialCapacity: Int): ByteBuffer = LanternByteBuffer(this.allocator.buffer(initialCapacity))
    override fun heapBuffer(): ByteBuffer = LanternByteBuffer(this.allocator.heapBuffer())
    override fun heapBuffer(initialCapacity: Int): ByteBuffer = LanternByteBuffer(this.allocator.heapBuffer(initialCapacity))
    override fun directBuffer(): ByteBuffer = LanternByteBuffer(this.allocator.directBuffer())
    override fun directBuffer(initialCapacity: Int): ByteBuffer = LanternByteBuffer(this.allocator.directBuffer(initialCapacity))

    companion object {

        @JvmField
        val DefaultPooled = LanternByteBufferAllocator(PooledByteBufAllocator.DEFAULT)
    }
}
