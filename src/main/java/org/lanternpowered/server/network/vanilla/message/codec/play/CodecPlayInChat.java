package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChatMessage;

public final class CodecPlayInChat implements Codec<MessagePlayInChatMessage> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayInChatMessage message) throws CodecException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MessagePlayInChatMessage decode(CodecContext context, ByteBuf buf) throws CodecException {
        return new MessagePlayInChatMessage(context.read(buf, String.class));
    }

}
