package org.lanternpowered.server.network.vanilla.message.codec.compression;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.caching.Caching;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.codec.object.VarInt;
import org.lanternpowered.server.network.vanilla.message.type.compression.MessageOutSetCompression;

@Caching
public final class CodecOutSetCompression implements Codec<MessageOutSetCompression> {

    @Override
    public ByteBuf encode(CodecContext context, MessageOutSetCompression message) throws CodecException {
        return context.write(context.byteBufAlloc().buffer(), VarInt.class, VarInt.of(message.getThreshold()));
    }

    @Override
    public MessageOutSetCompression decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new UnsupportedOperationException();
    }
}
