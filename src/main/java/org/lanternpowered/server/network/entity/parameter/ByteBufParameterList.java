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
package org.lanternpowered.server.network.entity.parameter;

import org.lanternpowered.server.network.buffer.ByteBuffer;
import org.lanternpowered.server.network.buffer.ByteBufferAllocator;

import javax.annotation.Nullable;

/**
 * A {@link ParameterList} which writes the content directly to
 * a {@link ByteBuffer}. The value of a specific {@link ParameterType}
 * cannot be overwritten by calling the method again, this will
 * result in an {@link IllegalStateException}.
 */
public class ByteBufParameterList extends AbstractParameterList {

    private final ByteBufferAllocator byteBufAllocator;
    @Nullable private ByteBuffer buf;

    public ByteBufParameterList(ByteBufferAllocator byteBufAllocator) {
        this.byteBufAllocator = byteBufAllocator;
    }

    private <T> void writeValueHeader(ParameterType<T> type) {
        if (this.buf == null) {
            this.buf = this.byteBufAllocator.buffer();
        }
        this.buf.writeByte(type.index);
        this.buf.writeByte(type.getValueType().getInternalId());
    }

    @Override
    public boolean isEmpty() {
        return this.buf == null;
    }

    @Override
    public <T> void add(ParameterType<T> type, T value) {
        this.writeValueHeader(type);
        //noinspection ConstantConditions
        type.getValueType().serialize(this.buf, value);
    }

    @Override
    public void add(ParameterType<Byte> type, byte value) {
        this.writeValueHeader(type);
        //noinspection ConstantConditions
        this.buf.writeByte(value);
    }

    @Override
    public void add(ParameterType<Integer> type, int value) {
        this.writeValueHeader(type);
        //noinspection ConstantConditions
        this.buf.writeVarInt(value);
    }

    @Override
    public void add(ParameterType<Float> type, float value) {
        this.writeValueHeader(type);
        //noinspection ConstantConditions
        this.buf.writeFloat(value);
    }

    @Override
    public void add(ParameterType<Boolean> type, boolean value) {
        this.writeValueHeader(type);
        //noinspection ConstantConditions
        this.buf.writeBoolean(value);
    }

    @Override
    public void write(ByteBuffer byteBuffer) {
        if (this.buf != null) {
            // Slice the ByteBuffer to avoid issues when this ParameterList is
            // used in multiple messages or in messages send to multiple players.
            byteBuffer.writeBytes(this.buf.slice());
        }
        byteBuffer.writeByte((byte) 0xff);
    }
}
