package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.caching.Caching;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutUpdateSign;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.text.Texts;

import com.flowpowered.math.vector.Vector3i;

@Caching
public final class CodecPlayOutUpdateSign implements Codec<MessagePlayOutUpdateSign> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutUpdateSign message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        context.write(buf, Vector3i.class, message.getPosition());
        Text[] lines = message.getLines();
        for (int i = 0; i < 4; i++) {
            context.write(buf, Text.class, lines[i] == null ? Texts.of() : lines[i]);
        }
        return buf;
    }

    @Override
    public MessagePlayOutUpdateSign decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }
}
