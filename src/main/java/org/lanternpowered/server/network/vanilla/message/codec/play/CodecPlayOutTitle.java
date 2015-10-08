package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTitle;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutTitle.SetTimes;

import org.spongepowered.api.text.Text;

public final class CodecPlayOutTitle implements Codec<MessagePlayOutTitle> {

    private static final int SET_TITLE = 0;
    private static final int SET_SUBTITLE = 1;
    private static final int SET_TIMES = 2;
    private static final int CLEAR = 3;
    private static final int RESET = 4;

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutTitle message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        if (message instanceof MessagePlayOutTitle.Clear) {
            context.writeVarInt(buf, CLEAR);
        } else if (message instanceof MessagePlayOutTitle.Reset) {
            context.writeVarInt(buf, RESET);
        } else if (message instanceof MessagePlayOutTitle.SetTitle) {
            context.writeVarInt(buf, SET_TITLE);
            context.write(buf, Text.class, ((MessagePlayOutTitle.SetTitle) message).getTitle());
        } else if (message instanceof MessagePlayOutTitle.SetSubtitle) {
            context.writeVarInt(buf, SET_SUBTITLE);
            context.write(buf, Text.class, ((MessagePlayOutTitle.SetSubtitle) message).getTitle());
        } else {
            MessagePlayOutTitle.SetTimes message0 = (SetTimes) message;
            context.writeVarInt(buf, SET_TIMES);
            buf.writeInt(message0.getFadeIn());
            buf.writeInt(message0.getStay());
            buf.writeInt(message0.getFadeOut());
        }
        return buf;
    }

    @Override
    public MessagePlayOutTitle decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }
}
