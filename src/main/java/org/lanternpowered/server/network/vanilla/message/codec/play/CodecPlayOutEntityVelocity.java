package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.caching.Caching;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityVelocity;

import com.flowpowered.math.vector.Vector3d;

@Caching
public final class CodecPlayOutEntityVelocity implements Codec<MessagePlayOutEntityVelocity> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutEntityVelocity message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        context.writeVarInt(buf, message.getEntityId());
        Vector3d velocity = message.getVelocity();
        buf.writeShort((short) Math.min(velocity.getX() * 8000d, Short.MAX_VALUE));
        buf.writeShort((short) Math.min(velocity.getY() * 8000d, Short.MAX_VALUE));
        buf.writeShort((short) Math.min(velocity.getZ() * 8000d, Short.MAX_VALUE));
        return null;
    }

    @Override
    public MessagePlayOutEntityVelocity decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }
}
