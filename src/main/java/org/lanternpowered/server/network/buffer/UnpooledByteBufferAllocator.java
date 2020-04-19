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
package org.lanternpowered.server.network.buffer;

import io.netty.buffer.Unpooled;

final class UnpooledByteBufferAllocator implements ByteBufferAllocator {

    static final UnpooledByteBufferAllocator INSTANCE = new UnpooledByteBufferAllocator();

    @Override
    public ByteBuffer buffer() {
        return new LanternByteBuffer(Unpooled.buffer());
    }

    @Override
    public ByteBuffer buffer(int initialCapacity) {
        return new LanternByteBuffer(Unpooled.buffer(initialCapacity));
    }

    @Override
    public ByteBuffer heapBuffer() {
        throw new UnsupportedOperationException("Heap buffers are not supported by the unpooled allocator.");
    }

    @Override
    public ByteBuffer heapBuffer(int initialCapacity) {
        throw new UnsupportedOperationException("Heap buffers are not supported by the unpooled allocator.");
    }

    @Override
    public ByteBuffer directBuffer() {
        return new LanternByteBuffer(Unpooled.directBuffer());
    }

    @Override
    public ByteBuffer directBuffer(int initialCapacity) {
        return new LanternByteBuffer(Unpooled.directBuffer(initialCapacity));
    }

    @Override
    public ByteBuffer wrappedBuffer(byte[] byteArray) {
        return new LanternByteBuffer(Unpooled.wrappedBuffer(byteArray));
    }
}
