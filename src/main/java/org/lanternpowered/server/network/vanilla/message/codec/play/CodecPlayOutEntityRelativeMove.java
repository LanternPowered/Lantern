package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityRelativeMove;

import com.flowpowered.math.vector.Vector3d;

public final class CodecPlayOutEntityRelativeMove implements Codec<MessagePlayOutEntityRelativeMove> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutEntityRelativeMove message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        context.writeVarInt(buf, message.getEntityId());
        Vector3d delta = message.getDelta();
        buf.writeByte((byte) (delta.getX() * 32.0));
        buf.writeByte((byte) (delta.getY() * 32.0));
        buf.writeByte((byte) (delta.getZ() * 32.0));
        buf.writeBoolean(message.isOnGround());
        return buf;
    }

    @Override
    public MessagePlayOutEntityRelativeMove decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }

}
