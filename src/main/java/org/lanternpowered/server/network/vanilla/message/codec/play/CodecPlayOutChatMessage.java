package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutChatMessage;
import org.lanternpowered.server.text.chat.LanternChatType;

import org.spongepowered.api.text.Text;

public class CodecPlayOutChatMessage implements Codec<MessagePlayOutChatMessage> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutChatMessage message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        context.write(buf, Text.class, message.getMessage());
        buf.writeByte(((LanternChatType) message.getChatType()).getInternalId());
        return buf;
    }

    @Override
    public MessagePlayOutChatMessage decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }

}
