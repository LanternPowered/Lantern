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

import com.google.common.io.ByteStreams;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.CodecException;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import org.lanternpowered.server.data.persistence.nbt.NbtDataContainerInputStream;
import org.lanternpowered.server.data.persistence.nbt.NbtStreamUtils;
import org.lanternpowered.server.network.item.NetworkItemHelper;
import org.lanternpowered.server.network.item.RawItemStack;
import org.spongepowered.api.ResourceKey;
import org.spongepowered.api.data.persistence.DataView;
import org.spongepowered.math.vector.Vector3d;
import org.spongepowered.math.vector.Vector3f;
import org.spongepowered.math.vector.Vector3i;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.OptionalInt;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.function.Supplier;

import org.checkerframework.checker.nullness.qual.Nullable;

@SuppressWarnings("OptionalUsedAsFieldOrParameterType")
public final class LanternByteBuffer implements ByteBuffer {

    private static final Charset UTF_8 = StandardCharsets.UTF_8;

    private final ByteBuf buf;

    public LanternByteBuffer(ByteBuf buf) {
        this.buf = buf;
    }

    public ByteBuf getDelegate() {
        return this.buf;
    }

    @Override
    public int capacity() {
        return this.buf.capacity();
    }

    @Override
    public int available() {
        return this.buf.readableBytes();
    }

    @Override
    public int refCnt() {
        return this.buf.refCnt();
    }

    @Override
    public OutputStream asOutputStream() {
        return new ByteBufOutputStream(this.buf);
    }

    @Override
    public InputStream asInputStream() {
        return new ByteBufInputStream(this.buf);
    }

    @Override
    public ByteBuffer retain() {
        this.buf.retain();
        return this;
    }

    @Override
    public ByteBuffer retain(int increment) {
        this.buf.retain(increment);
        return this;
    }

    @Override
    public ByteBuffer touch() {
        this.buf.touch();
        return this;
    }

    @Override
    public ByteBuffer touch(Object hint) {
        this.buf.touch(hint);
        return this;
    }

    @Override
    public int readerIndex() {
        return this.buf.readerIndex();
    }

    @Override
    public LanternByteBuffer readerIndex(int index) {
        this.buf.readerIndex(index);
        return this;
    }

    @Override
    public int writerIndex() {
        return this.buf.writerIndex();
    }

    @Override
    public LanternByteBuffer writerIndex(int index) {
        this.buf.writerIndex(index);
        return this;
    }

    @Override
    public LanternByteBuffer setIndex(int readIndex, int writeIndex) {
        this.buf.setIndex(readIndex, writeIndex);
        return this;
    }

    @Override
    public LanternByteBuffer clear() {
        this.buf.clear();
        return this;
    }

    @Override
    public LanternByteBuffer slice() {
        return new LanternByteBuffer(this.buf.slice());
    }

    @Override
    public LanternByteBuffer slice(int index, int length) {
        return new LanternByteBuffer(this.buf.slice(index, length));
    }

    @Override
    public boolean hasArray() {
        return this.buf.hasArray();
    }

    @Override
    public byte[] array() {
        return this.buf.array();
    }

    @Override
    public LanternByteBuffer writeBoolean(boolean data) {
        this.buf.writeBoolean(data);
        return this;
    }

    @Override
    public LanternByteBuffer setBoolean(int index, boolean data) {
        this.buf.setBoolean(index, data);
        return this;
    }

    @Override
    public boolean readBoolean() {
        return this.buf.readBoolean();
    }

    @Override
    public boolean getBoolean(int index) {
        return this.buf.getBoolean(index);
    }

    @Override
    public LanternByteBuffer writeByte(byte data) {
        this.buf.writeByte(data);
        return this;
    }

    @Override
    public LanternByteBuffer setByte(int index, byte data) {
        this.buf.setByte(index, data);
        return this;
    }

    @Override
    public byte readByte() {
        return this.buf.readByte();
    }

    @Override
    public byte getByte(int index) {
        return this.buf.getByte(index);
    }

    @Override
    public LanternByteBuffer writeByteArray(byte[] data) {
        writeVarInt(data.length);
        writeBytes(data);
        return this;
    }

    @Override
    public LanternByteBuffer writeByteArray(byte[] data, int start, int length) {
        writeVarInt(length);
        writeBytes(data, start, length);
        return this;
    }

    @Override
    public LanternByteBuffer setByteArray(int index, byte[] data) {
        final int oldIndex = this.buf.writerIndex();
        this.buf.writerIndex(index);
        writeByteArray(data);
        this.buf.writerIndex(oldIndex);
        return this;
    }

    @Override
    public LanternByteBuffer setByteArray(int index, byte[] data, int start, int length) {
        final int oldIndex = this.buf.writerIndex();
        this.buf.writerIndex(index);
        writeVarInt(length);
        writeBytes(data, start, length);
        this.buf.writerIndex(oldIndex);
        return this;
    }

    @Override
    public byte[] readLimitedByteArray(int maxLength) throws DecoderException {
        final int length = readVarInt();
        if (length < 0) {
            throw new DecoderException("Byte array length may not be negative.");
        }
        if (length > maxLength) {
            throw new DecoderException("Exceeded the maximum allowed length, got " + length + " which is greater then " + maxLength);
        }
        final byte[] bytes = new byte[length];
        this.buf.readBytes(bytes);
        return bytes;
    }

    @Override
    public byte[] readByteArray() {
        return readLimitedByteArray(Integer.MAX_VALUE);
    }

    @Override
    public byte[] readByteArray(int index) {
        final int oldIndex = this.buf.readerIndex();
        this.buf.readerIndex(index);
        final byte[] data = readByteArray();
        this.buf.readerIndex(oldIndex);
        return data;
    }

    @Override
    public LanternByteBuffer writeBytes(byte[] data) {
        this.buf.writeBytes(data);
        return this;
    }

    @Override
    public LanternByteBuffer writeBytes(byte[] data, int start, int length) {
        this.buf.writeBytes(data, start, length);
        return this;
    }

    @Override
    public LanternByteBuffer setBytes(int index, byte[] data) {
        final int oldIndex = this.buf.writerIndex();
        this.buf.writerIndex(index);
        this.buf.writeBytes(data);
        this.buf.writerIndex(oldIndex);
        return this;
    }

    @Override
    public LanternByteBuffer setBytes(int index, byte[] data, int start, int length) {
        final int oldIndex = this.buf.writerIndex();
        this.buf.writerIndex(index);
        this.buf.writeBytes(data, start, length);
        this.buf.writerIndex(oldIndex);
        return this;
    }

    @Override
    public byte[] readBytes(int length) {
        final byte[] data = new byte[length];
        this.buf.readBytes(data);
        return data;
    }

    @Override
    public byte[] readBytes(int index, int length) {
        final int oldIndex = this.buf.readerIndex();
        this.buf.readerIndex(index);
        final byte[] data = new byte[length];
        this.buf.readBytes(data);
        this.buf.readerIndex(oldIndex);
        return data;
    }

    @Override
    public LanternByteBuffer writeShort(short data) {
        this.buf.writeShort(data);
        return this;
    }

    @Override
    public LanternByteBuffer writeShortLE(short data) {
        this.buf.writeShortLE(data);
        return this;
    }

    @Override
    public LanternByteBuffer setShort(int index, short data) {
        this.buf.setShort(index, data);
        return this;
    }

    @Override
    public ByteBuffer setShortLE(int index, short data) {
        this.buf.setShortLE(index, data);
        return this;
    }

    @Override
    public short readShort() {
        return this.buf.readShort();
    }

    @Override
    public short readShortLE() {
        return this.buf.readShortLE();
    }

    @Override
    public short getShort(int index) {
        return this.buf.getShort(index);
    }

    @Override
    public short getShortLE(int index) {
        return this.buf.getShortLE(index);
    }

    @Override
    public LanternByteBuffer writeChar(char data) {
        this.buf.writeChar(data);
        return this;
    }

    @Override
    public LanternByteBuffer setChar(int index, char data) {
        this.buf.setChar(index, data);
        return this;
    }

    @Override
    public char readChar() {
        return this.buf.readChar();
    }

    @Override
    public char getChar(int index) {
        return this.buf.getChar(index);
    }

    @Override
    public LanternByteBuffer writeInt(int data) {
        this.buf.writeInt(data);
        return this;
    }

    @Override
    public LanternByteBuffer writeIntLE(int data) {
        this.buf.writeIntLE(data);
        return this;
    }

    @Override
    public LanternByteBuffer setInt(int index, int data) {
        this.buf.setInt(index, data);
        return this;
    }

    @Override
    public LanternByteBuffer setIntLE(int index, int data) {
        this.buf.setIntLE(index, data);
        return this;
    }

    @Override
    public int readInt() {
        return this.buf.readInt();
    }

    @Override
    public int readIntLE() {
        return this.buf.readIntLE();
    }

    @Override
    public int getInt(int index) {
        return this.buf.getInt(index);
    }

    @Override
    public int getIntLE(int index) {
        return this.buf.getIntLE(index);
    }

    @Override
    public LanternByteBuffer writeLong(long data) {
        this.buf.writeLong(data);
        return this;
    }

    @Override
    public LanternByteBuffer writeLongLE(long data) {
        this.buf.writeLongLE(data);
        return this;
    }

    @Override
    public LanternByteBuffer setLong(int index, long data) {
        this.buf.setLong(index, data);
        return this;
    }

    @Override
    public LanternByteBuffer setLongLE(int index, long data) {
        this.buf.setLongLE(index, data);
        return this;
    }

    @Override
    public long readLong() {
        return this.buf.readLong();
    }

    @Override
    public long readLongLE() {
        return this.buf.readLongLE();
    }

    @Override
    public long getLong(int index) {
        return this.buf.getLong(index);
    }

    @Override
    public long getLongLE(int index) {
        return this.buf.getLongLE(index);
    }

    @Override
    public LanternByteBuffer writeFloat(float data) {
        this.buf.writeFloat(data);
        return this;
    }

    @Override
    public LanternByteBuffer writeFloatLE(float data) {
        this.buf.writeFloatLE(data);
        return this;
    }

    @Override
    public LanternByteBuffer setFloat(int index, float data) {
        this.buf.setFloat(index, data);
        return this;
    }

    @Override
    public LanternByteBuffer setFloatLE(int index, float data) {
        this.buf.setFloatLE(index, data);
        return this;
    }

    @Override
    public float readFloat() {
        return this.buf.readFloat();
    }

    @Override
    public float readFloatLE() {
        return this.buf.readFloatLE();
    }

    @Override
    public float getFloat(int index) {
        return this.buf.getFloat(index);
    }

    @Override
    public float getFloatLE(int index) {
        return this.buf.getFloatLE(index);
    }

    @Override
    public LanternByteBuffer writeDouble(double data) {
        this.buf.writeDouble(data);
        return this;
    }

    @Override
    public LanternByteBuffer writeDoubleLE(double data) {
        this.buf.writeDoubleLE(data);
        return this;
    }

    @Override
    public LanternByteBuffer setDouble(int index, double data) {
        this.buf.setDouble(index, data);
        return this;
    }

    @Override
    public LanternByteBuffer setDoubleLE(int index, double data) {
        this.buf.setDoubleLE(index, data);
        return null;
    }

    @Override
    public double readDouble() {
        return this.buf.readDouble();
    }

    @Override
    public double readDoubleLE() {
        return this.buf.readDoubleLE();
    }

    @Override
    public double getDouble(int index) {
        return this.buf.getDouble(index);
    }

    @Override
    public double getDoubleLE(int index) {
        return this.buf.getDoubleLE(index);
    }

    /**
     * Writes a optional int to the {@link ByteBuf}.
     *
     * @param byteBuf The byte buffer
     * @param optValue The optional int value
     */
    public static void writeOptVarInt(ByteBuf byteBuf, OptionalInt optValue) {
        long value = 0;
        if (optValue.isPresent()) {
            value = optValue.getAsInt() << 1 | 1;
        }
        writeVarLong(byteBuf, value);
    }

    /**
     * Reads a optional int from the {@link ByteBuf}.
     *
     * @param byteBuf The byte buffer
     * @return The optional int value
     */
    public static OptionalInt readOptVarInt(ByteBuf byteBuf) {
        // We have a max of 35 bits, so we have 3 extra we can use,
        // one is now used to represent "empty".
        long value = readVarLong(byteBuf, 35);
        if ((value & 0x1) != 0) {
            return OptionalInt.of((int) (value >>> 1));
        }
        return OptionalInt.empty();
    }

    public static void writeVarInt(ByteBuf byteBuf, int value) {
        while ((value & 0xFFFFFF80) != 0L) {
            byteBuf.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        byteBuf.writeByte(value & 0x7F);
    }

    public static int readVarInt(ByteBuf byteBuf) {
        int value = 0;
        int i = 0;
        int b;
        while (((b = byteBuf.readByte()) & 0x80) != 0) {
            value |= (b & 0x7F) << i;
            i += 7;
            if (i > 35) {
                throw new DecoderException("Variable length is too long!");
            }
        }
        return value | (b << i);
    }

    /**
     * Reads a long value from the given {@link ByteBuf} in the varlong encoding.
     *
     * @param byteBuf The byte buf
     * @return The read long value
     */
    public static long readVarLong(ByteBuf byteBuf) {
        return readVarLong(byteBuf, 63);
    }

    /**
     * Reads a long value from the given {@link ByteBuf} in the varlong encoding. The
     * amount of read bits may not exceed the given limit.
     *
     * @param byteBuf The byte buf
     * @param maxBits The maximum amount of bits
     * @return The read long value
     */
    public static long readVarLong(ByteBuf byteBuf, int maxBits) {
        long value = 0L;
        int i = 0;
        long b;
        while (((b = byteBuf.readByte()) & 0x80L) != 0) {
            value |= (b & 0x7F) << i;
            i += 7;
            if (i > maxBits) {
                throw new DecoderException("Variable length is too long!");
            }
        }
        return value | (b << i);
    }

    public static void writeVarLong(ByteBuf byteBuf, long value) {
        while ((value & 0xFFFFFFFFFFFFFF80L) != 0L) {
            byteBuf.writeByte(((int) value & 0x7F) | 0x80);
            value >>>= 7;
        }
        byteBuf.writeByte((int) value & 0x7F);
    }

    @Override
    public LanternByteBuffer writeVarInt(int value) {
        writeVarInt(this.buf, value);
        return this;
    }

    @Override
    public LanternByteBuffer setVarInt(int index, int value) {
        int oldIndex = this.buf.writerIndex();
        this.buf.writerIndex(index);
        this.writeVarInt(value);
        this.buf.writerIndex(oldIndex);
        return this;
    }

    @Override
    public int readVarInt() {
        return readVarInt(this.buf);
    }

    @Override
    public int getVarInt(int index) {
        final int oldIndex = this.buf.readerIndex();
        this.buf.readerIndex(index);
        final int data = readVarInt();
        this.buf.readerIndex(oldIndex);
        return data;
    }

    @Override
    public LanternByteBuffer writeString(String data) {
        writeByteArray(data.getBytes(UTF_8));
        return this;
    }

    @Override
    public LanternByteBuffer setString(int index, String data) {
        setByteArray(index, data.getBytes(UTF_8));
        return this;
    }

    @Override
    public String readLimitedString(int maxLength) throws DecoderException {
        return new String(readLimitedByteArray(maxLength * 4), UTF_8);
    }

    @Override
    public String readString() {
        return readLimitedString(Short.MAX_VALUE);
    }

    @Override
    public String getString(int index) {
        return new String(readByteArray(index), UTF_8);
    }

    @Override
    public LanternByteBuffer writeUTF(String data) {
        final byte[] bytes = data.getBytes(UTF_8);
        if (bytes.length > 32767) {
            throw new EncoderException("String too big (was " + data.length() + " bytes encoded, max " + 32767 + ")");
        }
        this.buf.writeShort(bytes.length);
        this.buf.writeBytes(bytes);
        return this;
    }

    @Override
    public LanternByteBuffer setUTF(int index, String data) {
        return setAt(index, data, this::writeUTF);
    }

    @Override
    public String readUTF() {
        final int length = readShort();
        return new String(readBytes(length), UTF_8);
    }

    @Override
    public String getUTF(int index) {
        return getAt(index, this::readUTF);
    }

    @Override
    public LanternByteBuffer writeUniqueId(UUID data) {
        this.buf.writeLong(data.getMostSignificantBits());
        this.buf.writeLong(data.getLeastSignificantBits());
        return this;
    }

    @Override
    public LanternByteBuffer setUniqueId(int index, UUID data) {
        return setAt(index, data, this::writeUniqueId);
    }

    @Override
    public UUID readUniqueId() {
        final long most = this.buf.readLong();
        final long least = this.buf.readLong();
        return new UUID(most, least);
    }

    @Override
    public UUID getUniqueId(int index) {
        return getAt(index, this::readUniqueId);
    }

    @Override
    public LanternByteBuffer writeDataView(@Nullable DataView data) {
        if (data == null) {
            this.buf.writeByte(0);
            return this;
        }
        try {
            NbtStreamUtils.write(data, new ByteBufOutputStream(this.buf), false);
        } catch (IOException e) {
            throw new CodecException(e);
        }
        return this;
    }

    @Override
    public LanternByteBuffer setDataView(int index, @Nullable DataView data) {
        return setAt(index, data, this::writeDataView);
    }

    @Nullable
    @Override
    public DataView readLimitedDataView(int maximumDepth, int maxBytes) {
        final int index = this.buf.readerIndex();
        if (this.buf.readByte() == 0) {
            return null;
        }
        this.buf.readerIndex(index);
        try {
            try (NbtDataContainerInputStream input = new NbtDataContainerInputStream(
                    ByteStreams.limit(new ByteBufInputStream(this.buf), maxBytes), maximumDepth)) {
                return input.read();
            }
        } catch (IOException e) {
            throw new CodecException(e);
        }
    }

    @Nullable
    @Override
    public DataView readDataView() {
        return readLimitedDataView(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Nullable
    @Override
    public DataView getDataView(int index) {
        return getAt(index, this::readDataView);
    }

    @Override
    public ByteBuffer writeBytes(ByteBuffer byteBuffer) {
        this.buf.writeBytes(((LanternByteBuffer) byteBuffer).getDelegate());
        return this;
    }

    @Override
    public ByteBuffer readBytes(byte[] byteArray) {
        this.buf.readBytes(byteArray);
        return this;
    }

    @Override
    public ByteBuffer readBytes(byte[] dst, int dstIndex, int length) {
        this.buf.readBytes(dst, dstIndex, length);
        return this;
    }

    @Override
    public ByteBuffer readBytes(ByteBuffer byteBuffer) {
        this.buf.readBytes(((LanternByteBuffer) byteBuffer).getDelegate());
        return this;
    }

    @Override
    public ByteBuffer readBytes(ByteBuffer dst, int dstIndex, int length) {
        this.buf.readBytes(((LanternByteBuffer) dst).getDelegate(), dstIndex, length);
        return this;
    }

    @Override
    public LanternByteBuffer writeVarLong(long value) {
        writeVarLong(this.buf, value);
        return this;
    }

    @Override
    public LanternByteBuffer setVarLong(int index, long value) {
        final int oldIndex = this.buf.writerIndex();
        this.buf.writerIndex(index);
        writeVarLong(value);
        this.buf.writerIndex(oldIndex);
        return this;
    }

    @Override
    public long readVarLong() {
        return readVarLong(this.buf);
    }

    @Override
    public long getVarLong(int index) {
        final int oldIndex = this.buf.readerIndex();
        this.buf.readerIndex(index);
        final long data = readVarLong();
        this.buf.readerIndex(oldIndex);
        return data;
    }

    @Override
    public Vector3i getVector3i(int index) {
        return getAt(index, this::readVector3i);
    }

    @Override
    public LanternByteBuffer setVector3i(int index, Vector3i vector) {
        return setAt(index, vector, this::writeVector3i);
    }

    @Override
    public Vector3i readVector3i() {
        final int x = readInt();
        final int y = readInt();
        final int z = readInt();
        return new Vector3i(x, y, z);
    }

    @Override
    public LanternByteBuffer writeVector3i(int x, int y, int z) {
        writeInt(x);
        writeInt(y);
        writeInt(z);
        return this;
    }

    @Override
    public LanternByteBuffer writeVector3i(Vector3i vector) {
        return writeVector3i(vector.getX(), vector.getY(), vector.getZ());
    }

    @Override
    public Vector3i getPosition(int index) {
        return getAt(index, this::readPosition);
    }

    @Override
    public LanternByteBuffer setPosition(int index, Vector3i vector) {
        return setAt(index, vector, this::writePosition);
    }

    @Override
    public Vector3i readPosition() {
        final long value = this.buf.readLong();
        final int x = (int) (value >> 38);
        final int y = (int) (value & 0xfff);
        final int z = (int) (value << 38 >> 38) >> 12;
        return new Vector3i(x, y, z);
    }

    @Override
    public LanternByteBuffer writePosition(int x, int y, int z) {
        this.buf.writeLong(((long) x & 0x3ffffff) << 38 | ((long) z & 0x3ffffff) << 12 | ((long) y & 0xfff));
        return this;
    }

    @Override
    public LanternByteBuffer writePosition(Vector3i vector) {
        return writePosition(vector.getX(), vector.getY(), vector.getZ());
    }

    @Override
    public Vector3f getVector3f(int index) {
        return getAt(index, this::readVector3f);
    }

    @Override
    public LanternByteBuffer setVector3f(int index, Vector3f vector) {
        return setAt(index, vector, this::writeVector3f);
    }

    @Override
    public Vector3f readVector3f() {
        final float x = this.buf.readFloat();
        final float y = this.buf.readFloat();
        final float z = this.buf.readFloat();
        return new Vector3f(x, y, z);
    }

    @Override
    public LanternByteBuffer writeVector3f(float x, float y, float z) {
        this.buf.ensureWritable(Float.BYTES * 3);
        this.buf.writeFloat(x);
        this.buf.writeFloat(y);
        this.buf.writeFloat(z);
        return this;
    }

    @Override
    public LanternByteBuffer writeVector3f(Vector3f vector) {
        return writeVector3f(vector.getX(), vector.getY(), vector.getZ());
    }

    @Override
    public Vector3d getVector3d(int index) {
        return getAt(index, this::readVector3d);
    }

    @Override
    public LanternByteBuffer setVector3d(int index, Vector3d vector) {
        return setAt(index, vector, this::writeVector3d);
    }

    @Override
    public Vector3d readVector3d() {
        final double x = this.buf.readDouble();
        final double y = this.buf.readDouble();
        final double z = this.buf.readDouble();
        return new Vector3d(x, y, z);
    }

    @Override
    public LanternByteBuffer writeVector3d(double x, double y, double z) {
        this.buf.ensureWritable(Double.BYTES * 3);
        this.buf.writeDouble(x);
        this.buf.writeDouble(y);
        this.buf.writeDouble(z);
        return this;
    }

    @Override
    public LanternByteBuffer writeVector3d(Vector3d vector) {
        return writeVector3d(vector.getX(), vector.getY(), vector.getZ());
    }

    @Override
    public ResourceKey getResourceKey(int index) {
        return getAt(index, this::readResourceKey);
    }

    @Override
    public LanternByteBuffer setResourceKey(int index, ResourceKey ResourceKey) {
        return setAt(index, ResourceKey, this::writeResourceKey);
    }

    @Override
    public ResourceKey readResourceKey() {
        return ResourceKey.resolve(readString());
    }

    @Override
    public LanternByteBuffer writeResourceKey(ResourceKey ResourceKey) {
        if (ResourceKey.getNamespace().equals(ResourceKey.MINECRAFT_NAMESPACE)) {
            return writeString(ResourceKey.getValue());
        } else {
            return writeString(ResourceKey.toString());
        }
    }

    @Nullable
    @Override
    public RawItemStack getRawItemStack(int index) {
        return getAt(index, this::readRawItemStack);
    }

    @Override
    public LanternByteBuffer setRawItemStack(int index, @Nullable RawItemStack rawItemStack) {
        return setAt(index, rawItemStack, this::writeRawItemStack);
    }

    @Nullable
    @Override
    public RawItemStack readRawItemStack() {
        return NetworkItemHelper.readRawFrom(this);
    }

    @Override
    public LanternByteBuffer writeRawItemStack(@Nullable RawItemStack rawItemStack) {
        NetworkItemHelper.writeRawTo(this, rawItemStack);
        return this;
    }

    @Override
    public LanternByteBuffer ensureWritable(int minWritableBytes) {
        this.buf.ensureWritable(minWritableBytes);
        return this;
    }

    @Override
    public boolean release() {
        return this.buf.release();
    }

    @Override
    public boolean release(int decrement) {
        return this.buf.release(decrement);
    }

    @Override
    public LanternByteBuffer copy() {
        return new LanternByteBuffer(this.buf.copy());
    }

    private <T> T getAt(int index, Supplier<T> supplier) {
        final int oldIndex = this.buf.readerIndex();
        this.buf.readerIndex(index);
        final T data = supplier.get();
        this.buf.readerIndex(oldIndex);
        return data;
    }

    private <T> LanternByteBuffer setAt(int index, T object, Consumer<T> consumer) {
        final int oldIndex = this.buf.writerIndex();
        this.buf.writerIndex(index);
        consumer.accept(object);
        this.buf.writerIndex(oldIndex);
        return this;
    }
}
