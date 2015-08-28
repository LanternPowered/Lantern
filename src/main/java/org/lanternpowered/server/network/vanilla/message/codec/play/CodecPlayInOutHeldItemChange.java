package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInOutHeldItemChange;

/**
 * Note that incoming and outgoing codecs are not the same, but we use the same
 * message class.
 */
public final class CodecPlayInOutHeldItemChange implements Codec<MessagePlayInOutHeldItemChange> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayInOutHeldItemChange message) throws CodecException {
        return context.byteBufAlloc().buffer(1).writeByte(message.getSlot());
    }

    @Override
    public MessagePlayInOutHeldItemChange decode(CodecContext context, ByteBuf buf) throws CodecException {
        return new MessagePlayInOutHeldItemChange((byte) buf.readShort());
    }
}
