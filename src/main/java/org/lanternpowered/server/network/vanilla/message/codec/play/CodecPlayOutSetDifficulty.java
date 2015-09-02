package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.caching.Caching;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutSetDifficulty;

@Caching
public final class CodecPlayOutSetDifficulty implements Codec<MessagePlayOutSetDifficulty> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutSetDifficulty message) throws CodecException {
        return context.byteBufAlloc().buffer(1).writeByte(message.getDifficulty().getInternalId());
    }

    @Override
    public MessagePlayOutSetDifficulty decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new UnsupportedOperationException();
    }
}
