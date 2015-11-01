/*
 * This file is part of LanternServer, licensed under the MIT License (MIT).
 *
 * Copyright (c) LanternPowered <https://github.com/LanternPowered/LanternServer>
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
package org.lanternpowered.server.network.message.codec.object.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.handler.codec.CodecException;

import javax.annotation.Nullable;

public interface ObjectSerializerContext {

    /**
     * Gets the byte buf allocator.
     * 
     * @return the byte buf allocator
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
    <T> ByteBuf write(ByteBuf buf, Class<T> type, @Nullable T object) throws CodecException;

    /**
     * Attempts to write a object with the specified type at the specified buffer index.
     * 
     * @param buf the byte buffer
     * @param index the index to write the object at
     * @param type the type of the object
     * @param object the object instance, can be null depending on the type
     * @return the byte buffer for chaining
     */
    <T> ByteBuf writeAt(ByteBuf buf, int index, Class<T> type, @Nullable T object) throws CodecException;

    /**
     * Attempts to write a variable integer.
     * 
     * @param buf the byte buffer
     * @param value the value
     * @return the byte buffer for chaining
     */
    <T> ByteBuf writeVarInt(ByteBuf buf, int value) throws CodecException;

    /**
     * Attempts to write a variable integer at the specified buffer index.
     * 
     * @param buf the byte buffer
     * @param index the index to write the object at
     * @param value the value
     * @return the byte buffer for chaining
     */
    <T> ByteBuf writeVarIntAt(ByteBuf buf, int index, int value) throws CodecException;

    /**
     * Attempts to write a variable long.
     * 
     * @param buf the byte buffer
     * @param value the value
     * @return the byte buffer for chaining
     */
    <T> ByteBuf writeVarLong(ByteBuf buf, long value) throws CodecException;

    /**
     * Attempts to write a variable long at the specified buffer index.
     * 
     * @param buf the byte buffer
     * @param index the index to write the object at
     * @param value the value
     * @return the byte buffer for chaining
     */
    <T> ByteBuf writeVarLongAt(ByteBuf buf, int index, long value) throws CodecException;

    /**
     * Attempts to read a object with the specified type.
     * 
     * @param buf the byte buffer
     * @param type the type of the object
     * @return the object instance, can be null depending on the type
     */
    @Nullable
    <T> T read(ByteBuf buf, Class<T> type) throws CodecException;

    /**
     * Attempts to read a object with the specified type at the specified buffer index.
     * 
     * @param buf the byte buffer
     * @param index the index to read the object at
     * @param type the type of the object
     * @return the object instance, can be null depending on the type
     */
    @Nullable
    <T> T readAt(ByteBuf buf, int index, Class<T> type) throws CodecException;

    /**
     * Attempts to read a variable integer.
     * 
     * @param buf the byte buffer
     * @return the integer value
     */
    int readVarInt(ByteBuf buf) throws CodecException;

    /**
     * Attempts to read a variable integer at the specified buffer index.
     * 
     * @param buf the byte buffer
     * @param index the index to read the object at
     * @return the integer value
     */
    int readVarIntAt(ByteBuf buf, int index) throws CodecException;

    /**
     * Attempts to read a variable long.
     * 
     * @param buf the byte buffer
     * @return the integer value
     */
    long readVarLong(ByteBuf buf) throws CodecException;

    /**
     * Attempts to read a variable long at the specified buffer index.
     * 
     * @param buf the byte buffer
     * @param index the index to read the object at
     * @return the integer value
     */
    long readVarLongAt(ByteBuf buf, int index) throws CodecException;
}
