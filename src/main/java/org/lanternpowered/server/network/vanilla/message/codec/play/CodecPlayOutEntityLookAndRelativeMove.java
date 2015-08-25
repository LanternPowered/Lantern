package org.lanternpowered.server.network.vanilla.message.codec.play;

import static org.lanternpowered.server.network.vanilla.message.codec.play.CodecUtils.wrapAngle;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityLookAndRelativeMove;

import com.flowpowered.math.vector.Vector3d;

public final class CodecPlayOutEntityLookAndRelativeMove implements Codec<MessagePlayOutEntityLookAndRelativeMove> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutEntityLookAndRelativeMove message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        context.writeVarInt(buf, message.getEntityId());
        Vector3d delta = message.getDelta();
        buf.writeByte((byte) (delta.getX() * 32d));
        buf.writeByte((byte) (delta.getY() * 32d));
        buf.writeByte((byte) (delta.getZ() * 32d));
        buf.writeByte(wrapAngle(message.getYaw()));
        buf.writeByte(wrapAngle(message.getPitch()));
        buf.writeBoolean(message.isOnGround());
        return buf;
    }

    @Override
    public MessagePlayOutEntityLookAndRelativeMove decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }

}
