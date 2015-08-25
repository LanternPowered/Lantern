package org.lanternpowered.server.network.vanilla.message.codec.status;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.status.MessageStatusOutResponse;

public final class CodecStatusOutResponse implements Codec<MessageStatusOutResponse> {

    @Override
    public ByteBuf encode(CodecContext context, MessageStatusOutResponse message) throws CodecException {
        return context.write(context.byteBufAlloc().buffer(), String.class, message.getResponse());
    }

    @Override
    public MessageStatusOutResponse decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }

}
