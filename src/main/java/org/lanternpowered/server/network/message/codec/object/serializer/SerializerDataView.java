package org.lanternpowered.server.network.message.codec.object.serializer;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

import javax.annotation.Nullable;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufInputStream;
import io.netty.buffer.ByteBufOutputStream;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.data.io.nbt.NbtDataViewInputStream;
import org.lanternpowered.server.data.io.nbt.NbtDataViewOutputStream;

import org.spongepowered.api.data.DataView;

public class SerializerDataView implements ObjectSerializer<DataView> {

    @Override
    public void write(ObjectSerializerContext context, ByteBuf buf, @Nullable DataView object) throws CodecException {
        if (object == null) {
            buf.writeByte(0);
            return;
        }
        try {
            DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(new ByteBufOutputStream(buf)));
            NbtDataViewOutputStream ndvos = new NbtDataViewOutputStream(dos);
            ndvos.write(object);
            ndvos.flush();
            ndvos.close();
        } catch (IOException e) {
            throw new CodecException(e);
        }
    }

    @Nullable
    @Override
    public DataView read(ObjectSerializerContext context, ByteBuf buf) throws CodecException {
        int index = buf.readerIndex();
        if (buf.readByte() == 0) {
            return null;
        }
        buf.readerIndex(index);
        try {
            DataInputStream dis = new DataInputStream(new GZIPInputStream(new ByteBufInputStream(buf)));
            NbtDataViewInputStream ndvis = new NbtDataViewInputStream(dis);
            try {
                return ndvis.read();
            } finally {
                ndvis.close();
            }
        } catch (IOException e) {
            throw new CodecException(e);
        }
    }

}
