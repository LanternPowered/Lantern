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

interface ByteBufferAllocator {

    /**
     * Allocates a [ByteBuffer]. If it is a direct or heap buffer
     * depends on the actual implementation.
     *
     * @return The byte buffer
     */
    fun buffer(): ByteBuffer

    /**
     * Allocates a [ByteBuffer] with the specified initial capacity. If it is a
     * direct or heap buffer depends on the actual implementation.
     *
     * @return The byte buffer
     */
    fun buffer(initialCapacity: Int): ByteBuffer

    /**
     * Allocates a heap [ByteBuffer].
     *
     * @return The byte buffer
     */
    fun heapBuffer(): ByteBuffer

    /**
     * Allocates a heap [ByteBuffer] with the specified initial capacity.
     *
     * @return The byte buffer
     */
    fun heapBuffer(initialCapacity: Int): ByteBuffer

    /**
     * Allocates a direct [ByteBuffer].
     *
     * @return The byte buffer
     */
    fun directBuffer(): ByteBuffer

    /**
     * Allocates a direct [ByteBuffer] with the specified initial capacity.
     *
     * @return The byte buffer
     */
    fun directBuffer(initialCapacity: Int): ByteBuffer

    companion object {

        /**
         * The default pooled [ByteBufferAllocator].
         */
        val Pooled: ByteBufferAllocator
            get() = LanternByteBufferAllocator.DefaultPooled
    }
}
