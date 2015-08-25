package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.internal.MessagePlayOutChangeGameState;

public final class CodecPlayOutChangeGameState implements Codec<MessagePlayOutChangeGameState> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutChangeGameState message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        buf.writeByte((byte) message.getType());
        buf.writeFloat(message.getValue());
        return buf;
    }

    @Override
    public MessagePlayOutChangeGameState decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }

}
