/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://www.lanternpowered.org>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
 * Copyright (c) contributors
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the Software), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED AS IS, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
 * THE SOFTWARE.
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
