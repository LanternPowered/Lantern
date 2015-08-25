package org.lanternpowered.server.network.vanilla.message.codec.status;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.status.MessageStatusInRequest;

public final class CodecStatusInRequest implements Codec<MessageStatusInRequest> {

    @Override
    public ByteBuf encode(CodecContext context, MessageStatusInRequest message) throws CodecException {
        throw new CodecException();
    }

    @Override
    public MessageStatusInRequest decode(CodecContext context, ByteBuf buf) throws CodecException {
        return new MessageStatusInRequest();
    }

}
