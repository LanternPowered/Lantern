package org.lanternpowered.server.network.vanilla.message.codec.connection;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.codec.object.VarInt;
import org.lanternpowered.server.network.vanilla.message.type.connection.MessageInOutPing;

public final class CodecInOutPing implements Codec<MessageInOutPing> {

    @Override
    public ByteBuf encode(CodecContext context, MessageInOutPing message) throws CodecException {
        return context.write(context.byteBufAlloc().buffer(), VarInt.class, VarInt.of(message.getKeepAliveId()));
    }

    @Override
    public MessageInOutPing decode(CodecContext context, ByteBuf buf) throws CodecException {
        return new MessageInOutPing(context.read(buf, VarInt.class).value());
    }
}
