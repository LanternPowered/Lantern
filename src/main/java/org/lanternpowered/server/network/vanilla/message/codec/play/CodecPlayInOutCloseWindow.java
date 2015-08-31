package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutCloseWindow;

public final class CodecPlayInOutCloseWindow implements Codec<MessagePlayInOutCloseWindow> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayInOutCloseWindow message) throws CodecException {
        return context.byteBufAlloc().buffer(1).writeByte(message.getWindow());
    }

    @Override
    public MessagePlayInOutCloseWindow decode(CodecContext context, ByteBuf buf) throws CodecException {
        return new MessagePlayInOutCloseWindow(buf.readByte());
    }
}
