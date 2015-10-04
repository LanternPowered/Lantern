package org.lanternpowered.server.network.message.codec.object.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.object.VarLong;

public class SerializerVarLong implements ObjectSerializer<VarLong> {

    @Override
    public void write(ObjectSerializerContext context, ByteBuf buf, VarLong object) throws CodecException {
        long value = object.value();
        while ((value & 0xFFFFFFFFFFFFFF80L) != 0L) {
            buf.writeByte(((int) value & 0x7F) | 0x80);
            value >>>= 7;
        }
        buf.writeByte((int) value & 0x7F);
    }

    @Override
    public VarLong read(ObjectSerializerContext context, ByteBuf buf) throws CodecException {
        long value = 0L;
        int i = 0;
        long b;
        while (((b = buf.readByte()) & 0x80L) != 0) {
            value |= (b & 0x7F) << i;
            i += 7;
            if (i > 63) {
                throw new IllegalArgumentException("Variable length is too long!");
            }
        }
        return VarLong.of(value | (b << i));
    }
}
