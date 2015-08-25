package org.lanternpowered.server.network.message.codec.object.serializer;

import java.nio.charset.StandardCharsets;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

public class SerializerString implements ObjectSerializer<String> {

    @Override
    public void write(ObjectSerializerContext context, ByteBuf buf, String object) throws CodecException {
        byte[] bytes = object.getBytes(StandardCharsets.UTF_8);
        context.writeVarInt(buf, bytes.length);
        buf.writeBytes(bytes);
    }

    @Override
    public String read(ObjectSerializerContext context, ByteBuf buf) throws CodecException {
        int length = context.readVarInt(buf);
        byte[] bytes = new byte[length];
        buf.readBytes(bytes);
        return new String(bytes, StandardCharsets.UTF_8);
    }

}
