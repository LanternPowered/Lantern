package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.caching.Caching;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutEntityCollectItem;

@Caching
public final class CodecPlayOutEntityCollectItem implements Codec<MessagePlayOutEntityCollectItem> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutEntityCollectItem message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        context.writeVarInt(buf, message.getCollectedId());
        context.writeVarInt(buf, message.getCollectorId());
        return buf;
    }

    @Override
    public MessagePlayOutEntityCollectItem decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }
}
