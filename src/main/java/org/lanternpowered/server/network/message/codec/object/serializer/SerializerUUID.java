package org.lanternpowered.server.network.message.codec.object.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import java.util.UUID;

public class SerializerUUID implements ObjectSerializer<UUID> {

    @Override
    public void write(ObjectSerializerContext context, ByteBuf buf, UUID object) throws CodecException {
        buf.writeLong(object.getMostSignificantBits());
        buf.writeLong(object.getLeastSignificantBits());
    }

    @Override
    public UUID read(ObjectSerializerContext context, ByteBuf buf) throws CodecException {
        long most = buf.readLong();
        long least = buf.readLong();
        return new UUID(most, least);
    }

}
