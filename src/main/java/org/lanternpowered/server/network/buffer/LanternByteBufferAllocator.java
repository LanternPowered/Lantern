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

import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;

public class LanternByteBufferAllocator implements ByteBufferAllocator {

    static final LanternByteBufferAllocator DEFAULT_POOLED = new LanternByteBufferAllocator(PooledByteBufAllocator.DEFAULT);

    private final ByteBufAllocator byteBufAllocator;

    public LanternByteBufferAllocator(ByteBufAllocator byteBufAllocator) {
        this.byteBufAllocator = byteBufAllocator;
    }

    @Override
    public ByteBuffer buffer() {
        return new LanternByteBuffer(this.byteBufAllocator.buffer());
    }

    @Override
    public ByteBuffer buffer(int initialCapacity) {
        return new LanternByteBuffer(this.byteBufAllocator.buffer(initialCapacity));
    }

    @Override
    public ByteBuffer heapBuffer() {
        return new LanternByteBuffer(this.byteBufAllocator.heapBuffer());
    }

    @Override
    public ByteBuffer heapBuffer(int initialCapacity) {
        return new LanternByteBuffer(this.byteBufAllocator.heapBuffer(initialCapacity));
    }

    @Override
    public ByteBuffer directBuffer() {
        return new LanternByteBuffer(this.byteBufAllocator.directBuffer());
    }

    @Override
    public ByteBuffer directBuffer(int initialCapacity) {
        return new LanternByteBuffer(this.byteBufAllocator.directBuffer(initialCapacity));
    }

    @Override
    public ByteBuffer wrappedBuffer(byte[] byteArray) {
        // TODO: Use the byteBufAllocator?
        return new LanternByteBuffer(Unpooled.wrappedBuffer(byteArray));
    }
}
