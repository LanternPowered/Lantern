package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.caching.Caching;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutChatMessage;

import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;
import org.spongepowered.api.text.chat.ChatType;
import org.spongepowered.api.text.chat.ChatTypes;

@Caching
public final class CodecPlayOutChatMessage implements Codec<MessagePlayOutChatMessage> {

    @SuppressWarnings("deprecation")
    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutChatMessage message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        Text text = message.getMessage();
        ChatType type = message.getChatType();
        int value;
        if (type == ChatTypes.CHAT) {
            value = 0;
        } else if (type == ChatTypes.SYSTEM) {
            value = 1;
        } else if (type == ChatTypes.ACTION_BAR) {
            value = 2;
            // Fix the message format
            text = Texts.builder(Texts.legacy().to(text)).build();
        } else {
            throw new CodecException("Unknown chat type: " + type.getName());
        }
        context.write(buf, Text.class, message.getMessage());
        buf.writeByte(value);
        return buf;
    }

    @Override
    public MessagePlayOutChatMessage decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }
}
