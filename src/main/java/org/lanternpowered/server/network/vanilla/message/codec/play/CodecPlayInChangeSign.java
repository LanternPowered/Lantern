package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInChangeSign;
import org.spongepowered.api.text.Texts;

import com.flowpowered.math.vector.Vector3i;

public final class CodecPlayInChangeSign implements Codec<MessagePlayInChangeSign> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayInChangeSign message) throws CodecException {
        throw new CodecException();
    }

    @Override
    public MessagePlayInChangeSign decode(CodecContext context, ByteBuf buf) throws CodecException {
        Vector3i position = context.read(buf, Vector3i.class);
        String[] lines = new String[4];
        for (int i = 0; i < lines.length; i++) {
            // In the current protocol version are the lines send in json format,
            // this will change in 1.9
            lines[i] = Texts.toPlain(Texts.json().fromUnchecked(context.read(buf, String.class)));
        }
        return new MessagePlayInChangeSign(position, lines);
    }
}
