/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered>
 * Copyright (c) SpongePowered <https://www.spongepowered.org>
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
import io.netty.handler.codec.CodecException;

public interface SerializerContext {

    /**
     * Gets the {@link ByteBufAllocator}.
     *
     * @return the byte buf alloc
     */
    ByteBufAllocator byteBufAlloc();

    /**
     * Attempts to write a object with the specified type.
     *
     * @param buf the byte buffer
     * @param type the type of the object
     * @param object the object instance, can be null depending on the type
     * @return the byte buffer for chaining
     */
    <V> ByteBuf write(ByteBuf buf, Type<V> type, V object) throws CodecException;

    /**
     * Attempts to write a object with the specified type at the specified buffer index.
     *
     * @param buf the byte buffer
     * @param index the index to write the object at
     * @param type the type of the object
     * @param object the object instance, can be null depending on the type
     * @return the byte buffer for chaining
     */
    <V> ByteBuf writeAt(ByteBuf buf, int index, Type<V> type, V object) throws CodecException;

    /**
     * Attempts to write a variable integer.
     *
     * @param buf the byte buffer
     * @param value the value
     * @return the byte buffer for chaining
     */
    default ByteBuf writeVarInt(ByteBuf buf, int value) throws CodecException {
        return this.write(buf, Types.VAR_INT, value);
    }

    /**
     * Attempts to write a variable integer at the specified buffer index.
     *
     * @param buf the byte buffer
     * @param index the index to write the object at
     * @param value the value
     * @return the byte buffer for chaining
     */
    default ByteBuf writeVarIntAt(ByteBuf buf, int index, int value) throws CodecException {
        return this.writeAt(buf, index, Types.VAR_INT, value);
    }

    /**
     * Attempts to write a variable long.
     *
     * @param buf the byte buffer
     * @param value the value
     * @return the byte buffer for chaining
     */
    default ByteBuf writeVarLong(ByteBuf buf, long value) throws CodecException {
        return this.write(buf, Types.VAR_LONG, value);
    }

    /**
     * Attempts to write a variable long at the specified buffer index.
     *
     * @param buf the byte buffer
     * @param index the index to write the object at
     * @param value the value
     * @return the byte buffer for chaining
     */
    default ByteBuf writeVarLongAt(ByteBuf buf, int index, long value) throws CodecException {
        return this.writeAt(buf, index, Types.VAR_LONG, value);
    }

    /**
     * Attempts to read a object with the specified type.
     *
     * @param buf the byte buffer
     * @param type the type of the object
     * @return the object instance, can be null depending on the type
     */
    <V> V read(ByteBuf buf, Type<V> type) throws CodecException;

    /**
     * Attempts to read a object with the specified type at the specified buffer index.
     *
     * @param buf the byte buffer
     * @param index the index to read the object at
     * @param type the type of the object
     * @return the object instance, can be null depending on the type
     */
    <V> V readAt(ByteBuf buf, int index, Type<V> type) throws CodecException;

    /**
     * Attempts to read a variable integer.
     *
     * @param buf the byte buffer
     * @return the integer value
     */
    default int readVarInt(ByteBuf buf) throws CodecException {
        final Integer value = this.read(buf, Types.VAR_INT);
        return value == null ? 0 : value;
    }

    /**
     * Attempts to read a variable integer at the specified buffer index.
     *
     * @param buf the byte buffer
     * @param index the index to read the object at
     * @return the integer value
     */
    default int readVarIntAt(ByteBuf buf, int index) throws CodecException {
        final Integer value = this.readAt(buf, index, Types.VAR_INT);
        return value == null ? 0 : value;
    }

    /**
     * Attempts to read a variable long.
     *
     * @param buf the byte buffer
     * @return the integer value
     */
    default long readVarLong(ByteBuf buf) throws CodecException {
        final Long value = this.read(buf, Types.VAR_LONG);
        return value == null ? 0 : value;
    }

    /**
     * Attempts to read a variable long at the specified buffer index.
     *
     * @param buf the byte buffer
     * @param index the index to read the object at
     * @return the integer value
     */
    default long readVarLongAt(ByteBuf buf, int index) throws CodecException {
        final Long value = this.readAt(buf, index, Types.VAR_LONG);
        return value == null ? 0 : value;
    }

}
