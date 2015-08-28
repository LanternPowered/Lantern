package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.caching.Caching;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerHealthUpdate;

@Caching
public final class CodecPlayOutPlayerHealthUpdate implements Codec<MessagePlayOutPlayerHealthUpdate> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutPlayerHealthUpdate message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        buf.writeFloat(message.getHealth());
        context.writeVarInt(buf, (int) message.getFood());
        buf.writeFloat(message.getSaturation());
        return null;
    }

    @Override
    public MessagePlayOutPlayerHealthUpdate decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }
}
