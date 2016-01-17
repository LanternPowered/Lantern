/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) Contributors
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
package org.lanternpowered.server.network.message.codec.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.handler.codec.CodecException;

import java.util.Optional;

import javax.annotation.Nullable;

public class SimpleSerializerContext implements SerializerContext {

    public static final SerializerContext DEFAULT = new SimpleSerializerContext(
            PooledByteBufAllocator.DEFAULT, SerializerCollection.DEFAULT);

    private final SerializerCollection serializers;
    private final ByteBufAllocator byteBufAlloc;

    public SimpleSerializerContext(ByteBufAllocator byteBufAlloc, SerializerCollection serializers) {
        this.byteBufAlloc = byteBufAlloc;
        this.serializers = serializers;
    }

    @Override
    public ByteBufAllocator byteBufAlloc() {
        return this.byteBufAlloc;
    }

    @Override
    public <V> ByteBuf write(ByteBuf buf, Type<V> type, @Nullable V object) throws CodecException {
        final Optional<ValueSerializer<V>> optSerializer = this.serializers.get(type);
        if (!optSerializer.isPresent()) {
            throw new CodecException("Unable to find a value serializer for the specified type.");
        }
        optSerializer.get().write(this, buf, object);
        return buf;
    }

    @Override
    public <V> ByteBuf writeAt(ByteBuf buf, int index, Type<V> type, @Nullable V object) throws CodecException {
        int index0 = buf.writerIndex();
        buf.writerIndex(index);
        this.write(buf, type, object);
        buf.writerIndex(index0);
        return buf;
    }

    @Nullable
    @Override
    public <V> V read(ByteBuf buf, Type<V> type) throws CodecException {
        final Optional<ValueSerializer<V>> optSerializer = this.serializers.get(type);
        if (!optSerializer.isPresent()) {
            throw new CodecException("Unable to find a value serializer for the specified type.");
        }
        return optSerializer.get().read(this, buf);
    }

    @Nullable
    @Override
    public <V> V readAt(ByteBuf buf, int index, Type<V> type) throws CodecException {
        int index0 = buf.readerIndex();
        buf.readerIndex(index);
        V object = this.read(buf, type);
        buf.readerIndex(index0);
        return object;
    }

}
