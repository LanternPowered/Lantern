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
package org.lanternpowered.server.network.packet

import org.lanternpowered.server.network.NetworkContext
import org.lanternpowered.server.network.buffer.ByteBufferAllocator

interface CodecContext : NetworkContext {

    /**
     * Gets the [ByteBufferAllocator].
     *
     * @return The byte buffer allocator
     */
    fun byteBufAlloc(): ByteBufferAllocator
}
