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
package org.lanternpowered.server.network.buffer;

import io.netty.handler.codec.DecoderException;
import io.netty.util.ReferenceCounted;
import org.lanternpowered.server.network.item.RawItemStack;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.api.network.channel.ChannelBuf;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3f;
import org.spongepowered.math.vector.Vector3i;

import java.io.IOException;
import java.util.UUID;

import org.checkerframework.checker.nullness.qual.Nullable;

public interface ByteBuffer extends ChannelBuf, ReferenceCounted {

    @Override
    ByteBuffer ensureWritable(int minWritableBytes);

    @Override
    ByteBuffer retain();

    @Override
    ByteBuffer retain(int increment);

    @Override
    ByteBuffer touch();

    @Override
    ByteBuffer touch(Object hint);

    @Override
    ByteBuffer readerIndex(int index);

    @Override
    ByteBuffer writerIndex(int index);

    @Override
    ByteBuffer setIndex(int readIndex, int writeIndex);

    @Override
    ByteBuffer clear();

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
    ByteBuffer writeShortLE(short data);

    @Override
    ByteBuffer setShort(int index, short data);

    @Override
    ByteBuffer setShortLE(int index, short data);

    @Override
    ByteBuffer writeChar(char data);

    @Override
    ByteBuffer setChar(int index, char data);

    @Override
    ByteBuffer writeInt(int data);

    @Override
    ByteBuffer writeIntLE(int data);

    @Override
    ByteBuffer setInt(int index, int data);

    @Override
    ByteBuffer setIntLE(int index, int data);

    @Override
    ByteBuffer writeLong(long data);

    @Override
    ByteBuffer writeLongLE(long data);

    @Override
    ByteBuffer setLong(int index, long data);

    @Override
    ByteBuffer setLongLE(int index, long data);

    @Override
    ByteBuffer writeFloat(float data);

    @Override
    ByteBuffer writeFloatLE(float data);

    @Override
    ByteBuffer setFloat(int index, float data);

    @Override
    ByteBuffer setFloatLE(int index, float data);

    @Override
    ByteBuffer writeDouble(double data);

    @Override
    ByteBuffer writeDoubleLE(double data);

    @Override
    ByteBuffer setDouble(int index, double data);

    @Override
    ByteBuffer setDoubleLE(int index, double data);

    @Override
    ByteBuffer writeVarInt(int value);

    @Override
    ByteBuffer setVarInt(int index, int value);

    @Override
    ByteBuffer writeVarLong(long value);

    @Override
    ByteBuffer setVarLong(int index, long value);

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

    Vector3i getVector3i(int index);

    ByteBuffer setVector3i(int index, Vector3i vector);

    Vector3i readVector3i();

    ByteBuffer writeVector3i(int x, int y, int z);

    ByteBuffer writeVector3i(Vector3i vector);

    Vector3i getPosition(int index);

    ByteBuffer setPosition(int index, Vector3i vector);

    Vector3i readPosition();

    ByteBuffer writePosition(int x, int y, int z);

    ByteBuffer writePosition(Vector3i vector);

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

    ResourceKey getResourceKey(int index);

    ByteBuffer setResourceKey(int index, ResourceKey ResourceKey);

    ResourceKey readResourceKey();

    ByteBuffer writeResourceKey(ResourceKey ResourceKey);

    @Nullable
    RawItemStack getRawItemStack(int index);

    ByteBuffer setRawItemStack(int index, @Nullable RawItemStack rawItemStack);

    @Nullable
    RawItemStack readRawItemStack();

    ByteBuffer writeRawItemStack(@Nullable RawItemStack rawItemStack);

    ByteBuffer copy();
}
