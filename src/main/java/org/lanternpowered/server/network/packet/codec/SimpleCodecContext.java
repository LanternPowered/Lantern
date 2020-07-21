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

import io.netty.channel.Channel;
import org.lanternpowered.server.network.NetworkSession;
import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.ByteBufferAllocator;
import org.lanternpowered.server.network.buffer.contextual.ContextualValueType;

public class SimpleCodecContext implements CodecContext {

    private final ByteBufferAllocator byteBufferAlloc;
    private final NetworkSession session;
    private final Channel channel;

    public SimpleCodecContext(ByteBufferAllocator byteBufferAlloc, Channel channel, NetworkSession session) {
        this.byteBufferAlloc = byteBufferAlloc;
        this.channel = channel;
        this.session = session;
    }

    @Override
    public ByteBufferAllocator byteBufAlloc() {
        return this.byteBufferAlloc;
    }

    @Override
    public <T> void write(ByteBuffer buffer, ContextualValueType<T> type, T value) {
        type.write(this, value, buffer);
    }

    @Override
    public <V> V read(ByteBuffer buffer, ContextualValueType<V> type) {
        return type.read(this, buffer);
    }

    @Override
    public NetworkSession getSession() {
        return this.session;
    }

    @Override
    public Channel getChannel() {
        return this.channel;
    }
}
