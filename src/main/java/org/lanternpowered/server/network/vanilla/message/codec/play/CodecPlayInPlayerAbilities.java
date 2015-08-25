package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayInPlayerAbilities;

public final class CodecPlayInPlayerAbilities implements Codec<MessagePlayInPlayerAbilities> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayInPlayerAbilities message) throws CodecException {
        throw new CodecException();
    }

    @Override
    public MessagePlayInPlayerAbilities decode(CodecContext context, ByteBuf buf) throws CodecException {
        boolean flying = (buf.readByte() & 0x02) != 0;
        buf.readFloat();
        buf.readFloat();
        return new MessagePlayInPlayerAbilities(flying);
    }

}
