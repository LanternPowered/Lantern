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
package org.lanternpowered.server.network.packet.codec;

import org.lanternpowered.server.network.NetworkContext;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.ByteBufferAllocator;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueType;

public interface CodecContext extends NetworkContext {

    /**
     * Gets the {@link ByteBufferAllocator}.
     *
     * @return The byte buffer allocator
     */
    ByteBufferAllocator byteBufAlloc();

    /**
     * Writes the specified value for the {@link ContextualValueType}. The value may be
     * {@code null} depending on the value type.
     *
     * @param buffer The target byte buffer
     * @param type The type
     * @param value The value
     * @param <V> The value type
     */
    <V> void write(ByteBuffer buffer, ContextualValueType<V> type, V value);

    /**
     * Reads the specified value for the {@link ContextualValueType}. The value may be
     * {@code null} depending on the value type.
     *
     * @param buffer The target byte buffer
     * @param type The type
     * @param <V> The value type
     * @return The value
     */
    <V> V read(ByteBuffer buffer, ContextualValueType<V> type);

}
