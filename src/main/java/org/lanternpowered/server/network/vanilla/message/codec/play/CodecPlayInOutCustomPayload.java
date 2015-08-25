package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutChannelPayload;

public final class CodecPlayInOutCustomPayload implements Codec<MessagePlayInOutChannelPayload> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayInOutChannelPayload message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        context.write(buf, String.class, message.getChannel());
        buf.writeBytes(message.getContent());
        return buf;
    }

    @Override
    public MessagePlayInOutChannelPayload decode(CodecContext context, ByteBuf buf) throws CodecException {
        String channel = context.read(buf, String.class);
        ByteBuf content = context.byteBufAlloc().buffer(buf.readableBytes());
        buf.readBytes(content);
        return new MessagePlayInOutChannelPayload(channel, content);
    }

}
