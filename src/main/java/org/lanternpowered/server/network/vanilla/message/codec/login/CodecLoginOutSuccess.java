package org.lanternpowered.server.network.vanilla.message.codec.login;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginOutSuccess;

public final class CodecLoginOutSuccess implements Codec<MessageLoginOutSuccess> {

    @Override
    public ByteBuf encode(CodecContext context, MessageLoginOutSuccess message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        context.write(buf, String.class, message.getUniqueId().toString());
        context.write(buf, String.class, message.getUsername());
        return buf;
    }

    @Override
    public MessageLoginOutSuccess decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new UnsupportedOperationException();
    }

}
