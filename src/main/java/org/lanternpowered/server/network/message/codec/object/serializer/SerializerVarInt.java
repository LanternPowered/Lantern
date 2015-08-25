package org.lanternpowered.server.network.message.codec.object.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.object.VarInt;

public class SerializerVarInt implements ObjectSerializer<VarInt> {

    @Override
    public void write(ObjectSerializerContext context, ByteBuf buf, VarInt object) throws CodecException {
        int value = object.value();
        while ((value & 0xFFFFFF80) != 0L) {
            buf.writeByte((value & 0x7F) | 0x80);
            value >>>= 7;
        }
        buf.writeByte(value & 0x7F);
    }

    @Override
    public VarInt read(ObjectSerializerContext context, ByteBuf buf) throws CodecException {
        int value = 0;
        int i = 0;
        int b;
        while (((b = buf.readByte()) & 0x80) != 0) {
            value |= (b & 0x7F) << i;
            i += 7;
            if (i > 35) {
                throw new CodecException("Variable length is too long!");
            }
        }
        return VarInt.of(value | (b << i));
    }

}
