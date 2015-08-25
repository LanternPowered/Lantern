package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSendResourcePack;

public final class CodecPlayOutSendResourcePack implements Codec<MessagePlayOutSendResourcePack> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutSendResourcePack message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        context.write(buf, String.class, message.getUrl());
        context.write(buf, String.class, message.getHash());
        return buf;
    }

    @Override
    public MessagePlayOutSendResourcePack decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }

}
