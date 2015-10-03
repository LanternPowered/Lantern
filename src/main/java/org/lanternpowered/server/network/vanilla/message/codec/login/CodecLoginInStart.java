package org.lanternpowered.server.network.vanilla.message.codec.login;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.login.MessageLoginInStart;

public final class CodecLoginInStart implements Codec<MessageLoginInStart> {

    @Override
    public ByteBuf encode(CodecContext context, MessageLoginInStart message) throws CodecException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MessageLoginInStart decode(CodecContext context, ByteBuf buf) throws CodecException {
        return new MessageLoginInStart(context.read(buf, String.class));
    }
}
