package org.lanternpowered.server.network.buf;

import io.netty.buffer.ByteBuf;

import java.nio.ByteOrder;
import java.util.UUID;

import javax.annotation.Nullable;

import org.lanternpowered.server.network.message.codec.object.VarInt;
import org.lanternpowered.server.network.message.codec.object.serializer.ObjectSerializerContext;
import org.lanternpowered.server.network.message.codec.object.serializer.SerializerDataView;
import org.lanternpowered.server.network.message.codec.object.serializer.SerializerString;
import org.lanternpowered.server.network.message.codec.object.serializer.SerializerUUID;
import org.lanternpowered.server.network.message.codec.object.serializer.SerializerVarInt;
import org.lanternpowered.server.network.message.codec.object.serializer.SimpleObjectSerializerContext;
import org.lanternpowered.server.network.message.codec.object.serializer.SimpleObjectSerializers;
import org.spongepowered.api.data.DataView;
import org.spongepowered.api.network.ChannelBuf;

public class SimpleChannelBuf implements ChannelBuf {

    /**
     * A object serializer context specific for the sponge channel buffer.
     */
    private static final ObjectSerializerContext CONTEXT = new SimpleObjectSerializerContext(
            new SimpleObjectSerializers() {

                {
                    this.register(UUID.class, new SerializerUUID());
                    this.register(DataView.class, new SerializerDataView());
                    this.register(VarInt.class, new SerializerVarInt());
                    this.register(String.class, new SerializerString());
                }

            });


    private final ByteBuf buf;

    @Nullable
    private SimpleChannelBuf opposite;

    public SimpleChannelBuf(ByteBuf buf) {
        this.buf = buf;
    }

    @Override
    public int getCapacity() {
        return this.buf.capacity();
    }

    @Override
    public int available() {
        return this.buf.readableBytes();
    }

    @Override
    public SimpleChannelBuf order(ByteOrder order) {
        if (this.buf.order().equals(order)) {
            return this;
        } else {
            if (this.opposite == null) {
                this.opposite = new SimpleChannelBuf(this.buf.order(order));
                this.opposite.opposite = this;
            }
            return this.opposite;
        }
    }

    @Override
    public ByteOrder getByteOrder() {
        return this.buf.order();
    }

    @Override
    public int readerIndex() {
        return this.buf.readerIndex();
    }

    @Override
    public SimpleChannelBuf setReadIndex(int index) {
        this.buf.readerIndex(index);
        return this;
    }

    @Override
    public int writerIndex() {
        return this.buf.writerIndex();
    }

    @Override
    public SimpleChannelBuf setWriteIndex(int index) {
        this.buf.writerIndex(index);
        return this;
    }

    @Override
    public SimpleChannelBuf setIndex(int readIndex, int writeIndex) {
        this.buf.setIndex(readIndex, writeIndex);
        return this;
    }

    @Override
    public SimpleChannelBuf clear() {
        this.buf.clear();
        return this;
    }

    @Override
    public SimpleChannelBuf markRead() {
        this.buf.markReaderIndex();
        return this;
    }

    @Override
    public SimpleChannelBuf markWrite() {
        this.buf.markWriterIndex();
        return this;
    }

    @Override
    public SimpleChannelBuf resetRead() {
        this.buf.resetReaderIndex();
        return this;
    }

    @Override
    public SimpleChannelBuf resetWrite() {
        this.buf.resetWriterIndex();
        return this;
    }

    @Override
    public SimpleChannelBuf slice() {
        return new SimpleChannelBuf(this.buf.slice());
    }

    @Override
    public SimpleChannelBuf slice(int index, int length) {
        return new SimpleChannelBuf(this.buf.slice(index, length));
    }

    @Override
    public byte[] array() {
        return this.buf.array();
    }

    @Override
    public SimpleChannelBuf writeBoolean(boolean data) {
        this.buf.writeBoolean(data);
        return this;
    }

    @Override
    public SimpleChannelBuf setBoolean(int index, boolean data) {
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
    public SimpleChannelBuf writeByte(byte data) {
        this.buf.writeByte(data);
        return this;
    }

    @Override
    public SimpleChannelBuf setByte(int index, byte data) {
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
    public SimpleChannelBuf writeShort(short data) {
        this.buf.writeShort(data);
        return this;
    }

    @Override
    public SimpleChannelBuf setShort(int index, short data) {
        this.buf.setShort(index, data);
        return this;
    }

    @Override
    public short readShort() {
        return this.buf.readShort();
    }

    @Override
    public short getShort(int index) {
        return this.buf.getShort(index);
    }

    @Override
    public SimpleChannelBuf writeChar(char data) {
        this.buf.writeChar(data);
        return this;
    }

    @Override
    public SimpleChannelBuf setChar(int index, char data) {
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
    public SimpleChannelBuf writeInteger(int data) {
        this.buf.writeInt(data);
        return this;
    }

    @Override
    public SimpleChannelBuf setInteger(int index, int data) {
        this.buf.setInt(index, data);
        return this;
    }

    @Override
    public int readInteger() {
        return this.buf.readInt();
    }

    @Override
    public int getInteger(int index) {
        return this.buf.getInt(index);
    }

    @Override
    public SimpleChannelBuf writeLong(long data) {
        this.buf.writeLong(data);
        return this;
    }

    @Override
    public SimpleChannelBuf setLong(int index, long data) {
        this.buf.setLong(index, data);
        return this;
    }

    @Override
    public long readLong() {
        return this.buf.readLong();
    }

    @Override
    public long getLong(int index) {
        return this.buf.getLong(index);
    }

    @Override
    public SimpleChannelBuf writeFloat(float data) {
        this.buf.writeFloat(data);
        return this;
    }

    @Override
    public SimpleChannelBuf setFloat(int index, float data) {
        this.buf.setFloat(index, data);
        return this;
    }

    @Override
    public float readFloat() {
        return this.buf.readFloat();
    }

    @Override
    public float getFloat(int index) {
        return this.buf.getFloat(index);
    }

    @Override
    public SimpleChannelBuf writeDouble(double data) {
        this.buf.writeDouble(data);
        return this;
    }

    @Override
    public SimpleChannelBuf setDouble(int index, double data) {
        this.buf.setDouble(index, data);
        return this;
    }

    @Override
    public double readDouble() {
        return this.buf.readDouble();
    }

    @Override
    public double getDouble(int index) {
        return this.buf.getDouble(index);
    }

    @Override
    public SimpleChannelBuf writeString(String data) {
        CONTEXT.write(this.buf, String.class, data);
        return this;
    }

    @Override
    public SimpleChannelBuf setString(int index, String data) {
        CONTEXT.writeAt(this.buf, index, String.class, data);
        return this;
    }

    @Override
    public String readString() {
        return CONTEXT.read(this.buf, String.class);
    }

    @Override
    public String getString(int index) {
        return CONTEXT.readAt(this.buf, index, String.class);
    }

    @Override
    public SimpleChannelBuf writeUuid(UUID data) {
        CONTEXT.write(this.buf, UUID.class, data);
        return this;
    }

    @Override
    public SimpleChannelBuf setUuid(int index, UUID data) {
        CONTEXT.writeAt(this.buf, index, UUID.class, data);
        return this;
    }

    @Override
    public UUID readUuid() {
        return CONTEXT.read(this.buf, UUID.class);
    }

    @Override
    public UUID getUuid(int index) {
        return CONTEXT.readAt(this.buf, index, UUID.class);
    }

    @Override
    public SimpleChannelBuf writeDataView(DataView data) {
        CONTEXT.write(this.buf, DataView.class, data);
        return this;
    }

    @Override
    public SimpleChannelBuf setDataView(int index, DataView data) {
        CONTEXT.writeAt(this.buf, index, DataView.class, data);
        return this;
    }

    @Override
    public DataView readDataView() {
        return CONTEXT.read(this.buf, DataView.class);
    }

    @Override
    public DataView getDataView(int index) {
        return CONTEXT.readAt(this.buf, index, DataView.class);
    }
}
