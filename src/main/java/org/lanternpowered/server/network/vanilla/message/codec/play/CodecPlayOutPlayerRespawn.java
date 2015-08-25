package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerRespawn;

public final class CodecPlayOutPlayerRespawn implements Codec<MessagePlayOutPlayerRespawn> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutPlayerRespawn message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        buf.writeInt(message.getEnvironment().getId());
        buf.writeByte((byte) message.getDifficulty().getId());
        buf.writeByte((byte) message.getGameMode().getId());
        context.write(buf, String.class, "default"); // Not used
        return buf;
    }

    @Override
    public MessagePlayOutPlayerRespawn decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }

}
