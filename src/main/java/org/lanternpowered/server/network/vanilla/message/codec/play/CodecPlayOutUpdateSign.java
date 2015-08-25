package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutUpdateSign;

import com.flowpowered.math.vector.Vector3i;

public final class CodecPlayOutUpdateSign implements Codec<MessagePlayOutUpdateSign> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutUpdateSign message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        context.write(buf, Vector3i.class, message.getPosition());
        String[] lines = message.getLines();
        for (int i = 0; i < 4; i++) {
            String line = lines.length >= i ? null : lines[i];
            if (line == null) {
                line = "{\"text\":\"\"}";
            }
            context.write(buf, String.class, line);
        }
        return buf;
    }

    @Override
    public MessagePlayOutUpdateSign decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }

}
