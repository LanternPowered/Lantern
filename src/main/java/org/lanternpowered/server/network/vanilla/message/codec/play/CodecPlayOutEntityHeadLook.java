package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.caching.Caching;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityHeadLook;

import static org.lanternpowered.server.network.vanilla.message.codec.play.CodecUtils.wrapAngle;

@Caching
public final class CodecPlayOutEntityHeadLook implements Codec<MessagePlayOutEntityHeadLook> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutEntityHeadLook message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        context.writeVarInt(buf, message.getEntityId());
        buf.writeByte(wrapAngle(message.getYaw()));
        return buf;
    }

    @Override
    public MessagePlayOutEntityHeadLook decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }
}
