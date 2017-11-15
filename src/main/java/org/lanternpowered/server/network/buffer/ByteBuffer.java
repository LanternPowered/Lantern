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

import com.flowpowered.math.vector.Vector3d;
import com.flowpowered.math.vector.Vector3f;
import com.flowpowered.math.vector.Vector3i;
import io.netty.handler.codec.DecoderException;
import io.netty.util.ReferenceCounted;
import org.lanternpowered.server.network.item.RawItemStack;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.network.ChannelBuf;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteOrder;
import java.util.UUID;

import javax.annotation.Nullable;

public interface ByteBuffer extends ChannelBuf, ReferenceCounted {

    OutputStream asOutputStream();

    @Override
    ByteBuffer retain();

    @Override
    ByteBuffer retain(int increment);

    @Override
    ByteBuffer touch();

    @Override
    ByteBuffer touch(Object hint);

    @Override
    ByteBuffer order(ByteOrder order);

    @Override
    ByteBuffer setReadIndex(int index);

    @Override
    ByteBuffer setWriteIndex(int index);

    @Override
    ByteBuffer setIndex(int readIndex, int writeIndex);

    @Override
    ByteBuffer clear();

    @Override
    ByteBuffer markRead();

    @Override
    ByteBuffer markWrite();

    @Override
    ByteBuffer resetRead();

    @Override
    ByteBuffer resetWrite();

    @Override
    ByteBuffer slice();

    @Override
    ByteBuffer slice(int index, int length);

    @Override
    ByteBuffer writeBoolean(boolean data);

    @Override
    ByteBuffer setBoolean(int index, boolean data);

    @Override
    ByteBuffer writeByte(byte data);

    @Override
    ByteBuffer setByte(int index, byte data);

    /**
     * Reads a byte array and checks whether the length isn't
     * longer then the specified maximum length.
     *
     * @param maxLength The maximum length
     * @return The byte array
     * @throws DecoderException If the length of the byte
     *     array exceeded the specified maximum length
     */
    byte[] readLimitedByteArray(int maxLength) throws DecoderException;

    @Override
    ByteBuffer writeByteArray(byte[] data);

    @Override
    ByteBuffer writeByteArray(byte[] data, int start, int length);

    @Override
    ByteBuffer setByteArray(int index, byte[] data);

    @Override
    ByteBuffer setByteArray(int index, byte[] data, int start, int length);

    @Override
    ByteBuffer writeBytes(byte[] data);

    @Override
    ByteBuffer writeBytes(byte[] data, int start, int length);

    @Override
    ByteBuffer setBytes(int index, byte[] data);

    @Override
    ByteBuffer setBytes(int index, byte[] data, int start, int length);

    @Override
    ByteBuffer writeShort(short data);

    @Override
    ByteBuffer setShort(int index, short data);

    @Override
    ByteBuffer writeChar(char data);

    @Override
    ByteBuffer setChar(int index, char data);

    @Override
    ByteBuffer writeInteger(int data);

    @Override
    ByteBuffer setInteger(int index, int data);

    @Override
    ByteBuffer writeLong(long data);

    @Override
    ByteBuffer setLong(int index, long data);

    @Override
    ByteBuffer writeFloat(float data);

    @Override
    ByteBuffer setFloat(int index, float data);

    @Override
    ByteBuffer writeDouble(double data);

    @Override
    ByteBuffer setDouble(int index, double data);

    @Override
    ByteBuffer writeVarInt(int value);

    @Override
    ByteBuffer setVarInt(int index, int value);

    /**
     * Reads a string and checks whether the length isn't longer
     * then the specified maximum length.
     *
     * @param maxLength The maximum length
     * @return The byte array
     * @throws DecoderException If the length of the byte
     *     array exceeded the specified maximum length
     */
    String readLimitedString(int maxLength) throws DecoderException;

    @Override
    ByteBuffer writeString(String data);

    @Override
    ByteBuffer setString(int index, String data);

    @Override
    ByteBuffer writeUTF(String data);

    @Override
    ByteBuffer setUTF(int index, String data);

    @Override
    ByteBuffer writeUniqueId(UUID data);

    @Override
    ByteBuffer setUniqueId(int index, UUID data);

    @Override
    ByteBuffer writeDataView(@Nullable DataView data);

    @Override
    ByteBuffer setDataView(int index, @Nullable DataView data);

    /**
     * Reads a {@link DataView} and checks whether the depth of the underlying
     * tree doesn't get bigger then the specified maximum depth and is not bigger
     * then the maximum amount of bytes.
     *
     * @param maxDepth The maximum depth
     * @param maxBytes The maximum amount of bytes
     * @return The data view
     * @throws IOException If the depth exceeded the maximum depth or
     *     if the size exceeded the maximum bytes
     */
    @Nullable
    DataView readLimitedDataView(int maxDepth, int maxBytes);

    @Nullable
    @Override
    DataView readDataView();

    @Nullable
    @Override
    DataView getDataView(int index);

    ByteBuffer writeBytes(ByteBuffer byteBuffer);

    /**
     * Transfers this buffer's data to the specified byte array, starting
     * from the current readerIndex. Transferring until either this buffer
     * is at the end of the reader index or the specified array is filled.
     *
     * @param dst The destination byte array
     * @return This stream for chaining
     */
    ByteBuffer readBytes(byte[] dst);

    ByteBuffer readBytes(byte[] dst, int dstIndex, int length);

    /**
     * Transfers this buffer's data to the specified byte buffer, starting
     * from the current readerIndex.
     *
     * @param byteBuffer The target byte buffer
     * @return This stream for chaining
     */
    ByteBuffer readBytes(ByteBuffer byteBuffer);

    ByteBuffer readBytes(ByteBuffer dst, int dstIndex, int length);

    /**
     * Sets the specified varlong at the current writerIndex and increases the
     * writerIndex by the number of bytes written.
     *
     * <p>The number of bytes written depends on the size of the value.</p>
     *
     * @param value The varlong value
     * @return This stream for chaining
     */
    ByteBuffer writeVarLong(long value);

    /**
     * Sets the specified varlong at the specified absolute index in this buffer.
     * This method does not modify readerIndex or writerIndex of this buffer.
     *
     * <p>The number of bytes written depends on the size of the value.</p>
     *
     * @param index The index
     * @param value The varlong value
     * @return This stream for chaining
     */
    ByteBuffer setVarLong(int index, long value);

    /**
     * Gets a varlong at the current readerIndex and increases the readerIndex by
     * the number of bytes read.
     *
     * <p>The number of bytes read depends on the size of the value.</p>
     *
     * @return The varlong value
     */
    long readVarLong();

    /**
     * Gets a varlong at the specified absolute index in this buffer.
     *
     * <p>The number of bytes read depends on the size of the value.</p>
     *
     * @param index The index
     * @return The varlong value
     */
    long getVarLong(int index);

    Vector3i getVector3i(int index);

    ByteBuffer setVector3i(int index, Vector3i vector);

    Vector3i readVector3i();

    ByteBuffer writeVector3i(int x, int y, int z);

    ByteBuffer writeVector3i(Vector3i vector);

    Vector3f getVector3f(int index);

    ByteBuffer setVector3f(int index, Vector3f vector);

    Vector3f readVector3f();

    ByteBuffer writeVector3f(float x, float y, float z);

    ByteBuffer writeVector3f(Vector3f vector);

    Vector3d getVector3d(int index);

    ByteBuffer setVector3d(int index, Vector3d vector);

    Vector3d readVector3d();

    ByteBuffer writeVector3d(double x, double y, double z);

    ByteBuffer writeVector3d(Vector3d vector);

    @Nullable
    RawItemStack getRawItemStack(int index);

    ByteBuffer setRawItemStack(int index, @Nullable RawItemStack rawItemStack);

    @Nullable
    RawItemStack readRawItemStack();

    ByteBuffer writeRawItemStack(@Nullable RawItemStack rawItemStack);

    /**
     * Makes sure the number of the writable bytes is equal to
     * or greater than the specified value.
     *
     * @param minWritableBytes The minimum writable bytes
     * @return This stream for chaining
     */
    ByteBuffer ensureWritable(int minWritableBytes);

    ByteBuffer copy();

    int readableBytes();
}
