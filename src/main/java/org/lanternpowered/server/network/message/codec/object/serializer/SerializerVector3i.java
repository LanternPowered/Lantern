package org.lanternpowered.server.network.message.codec.object.serializer;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import com.flowpowered.math.vector.Vector3i;

public class SerializerVector3i implements ObjectSerializer<Vector3i> {

    @Override
    public void write(ObjectSerializerContext context, ByteBuf buf, Vector3i object) throws CodecException {
        int x = object.getX();
        int y = object.getY();
        int z = object.getZ();
        buf.writeLong((x & 0x3ffffff) << 38 | (y & 0xfff) << 26 | (z & 0x3ffffff));
    }

    @Override
    public Vector3i read(ObjectSerializerContext context, ByteBuf buf) throws CodecException {
        long value = buf.readLong();
        int x = (int) (value >> 38);
        int y = (int) (value << 26 >> 52);
        int z = (int) (value << 38 >> 38);
        return new Vector3i(x, y, z);
    }

}
