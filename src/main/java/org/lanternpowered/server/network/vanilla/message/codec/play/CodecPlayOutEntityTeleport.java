package org.lanternpowered.server.network.vanilla.message.codec.play;

import static org.lanternpowered.server.network.vanilla.message.codec.play.CodecUtils.wrapAngle;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityTeleport;

import com.flowpowered.math.vector.Vector3d;

public final class CodecPlayOutEntityTeleport implements Codec<MessagePlayOutEntityTeleport> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutEntityTeleport message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        context.writeVarInt(buf, message.getEntityId());
        Vector3d position = message.getPosition();
        buf.writeInt((byte) (position.getX() * 32d));
        buf.writeInt((byte) (position.getY() * 32d));
        buf.writeInt((byte) (position.getZ() * 32d));
        buf.writeByte(wrapAngle(message.getYaw()));
        buf.writeByte(wrapAngle(message.getPitch()));
        buf.writeBoolean(message.isOnGround());
        return buf;
    }

    @Override
    public MessagePlayOutEntityTeleport decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }

}
