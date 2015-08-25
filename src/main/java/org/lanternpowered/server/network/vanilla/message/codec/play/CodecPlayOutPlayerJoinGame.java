package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerJoinGame;

public final class CodecPlayOutPlayerJoinGame implements Codec<MessagePlayOutPlayerJoinGame> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutPlayerJoinGame message) throws CodecException {
        ByteBuf buf = context.byteBufAlloc().buffer();
        buf.writeInt(message.getEntityId());
        buf.writeByte((byte) message.getGameMode().getId());
        buf.writeByte((byte) message.getEnvironment().getId());
        buf.writeByte((byte) message.getDifficulty().getId());
        buf.writeByte((byte) Math.min(message.getPlayerListSize(), 255));
        context.write(buf, String.class, "default"); // Not used
        buf.writeBoolean(message.getReducedDebug());
        return buf;
    }

    @Override
    public MessagePlayOutPlayerJoinGame decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }

}
