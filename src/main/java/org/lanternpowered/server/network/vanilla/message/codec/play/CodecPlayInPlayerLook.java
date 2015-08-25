package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerLook;

public final class CodecPlayInPlayerLook implements Codec<MessagePlayInPlayerLook> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayInPlayerLook message) throws CodecException {
        throw new CodecException();
    }

    @Override
    public MessagePlayInPlayerLook decode(CodecContext context, ByteBuf buf) throws CodecException {
        float yaw = buf.readFloat();
        float pitch = buf.readFloat();
        boolean onGround = buf.readBoolean();
        return new MessagePlayInPlayerLook(yaw, pitch , onGround);
    }

}
