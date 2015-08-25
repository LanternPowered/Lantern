package org.lanternpowered.server.network.message.codec.object.serializer;

import org.lanternpowered.server.network.message.codec.object.VarInt;
import org.lanternpowered.server.network.message.codec.object.VarLong;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

public abstract class AbstractObjectSerializerContext implements ObjectSerializerContext {

    private final ObjectSerializers objectSerializers;

    public AbstractObjectSerializerContext(ObjectSerializers objectSerializers) {
        this.objectSerializers = objectSerializers;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> ByteBuf write(ByteBuf buf, Class<T> type, T object) throws CodecException {
        ObjectSerializer<T> objectSerializer = (ObjectSerializer<T>) this.objectSerializers.find(type);
        if (objectSerializer == null) {
            throw new CodecException("Unable to find a object serializer for: " + type.getName());
        }
        objectSerializer.write(this, buf, object);
        return buf;
    }

    @Override
    public <T> ByteBuf writeAt(ByteBuf buf, int index, Class<T> type, T object) throws CodecException {
        int index0 = buf.writerIndex();
        buf.writerIndex(index);
        this.write(buf, type, object);
        buf.writerIndex(index0);
        return buf;
    }

    @Override
    public <T> ByteBuf writeVarInt(ByteBuf buf, int value) throws CodecException {
        return this.write(buf, VarInt.class, VarInt.of(value));
    }

    @Override
    public <T> ByteBuf writeVarIntAt(ByteBuf buf, int index, int value) throws CodecException {
        return this.writeAt(buf, index, VarInt.class, VarInt.of(value));
    }

    @Override
    public <T> ByteBuf writeVarLong(ByteBuf buf, long value) throws CodecException {
        return this.write(buf, VarLong.class, VarLong.of(value));
    }

    @Override
    public <T> ByteBuf writeVarLongAt(ByteBuf buf, int index, long value) throws CodecException {
        return this.writeAt(buf, index, VarLong.class, VarLong.of(value));
    }

    @SuppressWarnings("unchecked")
    @Override
    public <T> T read(ByteBuf buf, Class<T> type) throws CodecException {
        ObjectSerializer<T> objectSerializer = (ObjectSerializer<T>) this.objectSerializers.find(type);
        if (objectSerializer == null) {
            throw new CodecException("Unable to find a object serializer for: " + type.getName());
        }
        return objectSerializer.read(this, buf);
    }

    @Override
    public <T> T readAt(ByteBuf buf, int index, Class<T> type) throws CodecException {
        int index0 = buf.readerIndex();
        buf.readerIndex(index);
        T object = this.read(buf, type);
        buf.readerIndex(index0);
        return object;
    }

    @Override
    public int readVarInt(ByteBuf buf) throws CodecException {
        return this.read(buf, VarInt.class).value();
    }

    @Override
    public int readVarIntAt(ByteBuf buf, int index) throws CodecException {
        return this.readAt(buf, index, VarInt.class).value();
    }

    @Override
    public long readVarLong(ByteBuf buf) throws CodecException {
        return this.read(buf, VarLong.class).value();
    }

    @Override
    public long readVarLongAt(ByteBuf buf, int index) throws CodecException {
        return this.readAt(buf, index, VarLong.class).value();
    }

}
