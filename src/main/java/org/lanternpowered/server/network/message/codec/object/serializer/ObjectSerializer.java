package org.lanternpowered.server.network.message.codec.object.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

public interface ObjectSerializer<T> {

    void write(ObjectSerializerContext context, ByteBuf buf, T object) throws CodecException;

    T read(ObjectSerializerContext context, ByteBuf buf) throws CodecException;
}
