package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutWorldTime;

public final class CodecPlayOutWorldTime implements Codec<MessagePlayOutWorldTime> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutWorldTime message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();

        // The time also uses a negative tag
        long time = Math.abs(message.getTime());
        while (time >= 24000) {
            time -= 24000;
        }
        while (time < 0) {
            time += 24000;
        }
        if (!message.getEnabled()) {
            time = time == 0 ? -1 : -time;
        }
        long age = time + message.getMoonPhase().ordinal() * 24000;

        buf.writeLong(age);
        buf.writeLong(time);

        return buf;
    }

    @Override
    public MessagePlayOutWorldTime decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new UnsupportedOperationException();
    }

}
