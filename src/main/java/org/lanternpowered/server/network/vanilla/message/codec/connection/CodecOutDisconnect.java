package org.lanternpowered.server.network.vanilla.message.codec.connection;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageOutDisconnect;

public final class CodecOutDisconnect implements Codec<MessageOutDisconnect> {

    @Override
    public ByteBuf encode(CodecContext context, MessageOutDisconnect message) throws CodecException {
        return context.write(context.byteBufAlloc().buffer(), String.class, message.getJson());
    }

    @Override
    public MessageOutDisconnect decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new UnsupportedOperationException();
    }

}
