package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.message.codec.object.VarInt;
import org.lanternpowered.server.network.vanilla.message.type.play.internal.MessagePlayInPlayerAction;

public final class CodecPlayInPlayerAction implements Codec<MessagePlayInPlayerAction> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayInPlayerAction message) throws CodecException {
        throw new UnsupportedOperationException();
    }

    @Override
    public MessagePlayInPlayerAction decode(CodecContext context, ByteBuf buf) throws CodecException {
        // Normally should this be the entity id, but only the
        // client player will send this, so it won't be used
        context.read(buf, VarInt.class);
        int action = context.read(buf, VarInt.class).value();
        int value = context.read(buf, VarInt.class).value();
        return new MessagePlayInPlayerAction(action, value);
    }

}
