package org.lanternpowered.server.network.vanilla.message.codec.status;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.status.MessageStatusInOutPing;

public final class CodecStatusInOutPing implements Codec<MessageStatusInOutPing> {

    @Override
    public ByteBuf encode(CodecContext context, MessageStatusInOutPing message) throws CodecException {
        return context.byteBufAlloc().buffer(8).writeLong(message.getTime());
    }

    @Override
    public MessageStatusInOutPing decode(CodecContext context, ByteBuf buf) throws CodecException {
        return new MessageStatusInOutPing(buf.readLong());
    }

}
