package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTabListHeaderAndFooter;
import org.spongepowered.api.text.Text;

public final class CodecPlayOutTabListHeaderAndFooter implements Codec<MessagePlayOutTabListHeaderAndFooter> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutTabListHeaderAndFooter message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        context.write(buf, Text.class, message.getHeader());
        context.write(buf, Text.class, message.getFooter());
        return buf;
    }

    @Override
    public MessagePlayOutTabListHeaderAndFooter decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }
}
