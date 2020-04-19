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

public interface ByteBufferAllocator {

    /**
     * Gets the unpooled {@link ByteBufferAllocator}, it does not support
     * heap {@link ByteBuffer}s.
     *
     * @return The unpooled byte buffer allocator
     */
    static ByteBufferAllocator unpooled() {
        return UnpooledByteBufferAllocator.INSTANCE;
    }

    /**
     * Gets the default pooled {@link ByteBufferAllocator}.
     *
     * @return The pooled byte buffer allocator
     */
    static ByteBufferAllocator pooled() {
        return LanternByteBufferAllocator.DEFAULT_POOLED;
    }

    /**
     * Allocates a {@link ByteBuffer}. If it is a direct or heap buffer
     * depends on the actual implementation.
     *
     * @return The byte buffer
     */
    ByteBuffer buffer();

    /**
     * Allocates a {@link ByteBuffer} with the specified initial capacity. If it is a
     * direct or heap buffer depends on the actual implementation.
     *
     * @return The byte buffer
     */
    ByteBuffer buffer(int initialCapacity);

    /**
     * Allocates a heap {@link ByteBuffer}.
     *
     * @return The byte buffer
     */
    ByteBuffer heapBuffer();

    /**
     * Allocates a heap {@link ByteBuffer} with the specified initial capacity.
     *
     * @return The byte buffer
     */
    ByteBuffer heapBuffer(int initialCapacity);

    /**
     * Allocates a direct {@link ByteBuffer}.
     *
     * @return The byte buffer
     */
    ByteBuffer directBuffer();

    /**
     * Allocates a direct {@link ByteBuffer} with the specified initial capacity.
     *
     * @return The byte buffer
     */
    ByteBuffer directBuffer(int initialCapacity);

    /**
     * Creates a {@link ByteBuffer} that wraps around the specified byte array.
     *
     * @param byteArray The byte array
     * @return The wrapped byte buffer
     */
    ByteBuffer wrappedBuffer(byte[] byteArray);
}
