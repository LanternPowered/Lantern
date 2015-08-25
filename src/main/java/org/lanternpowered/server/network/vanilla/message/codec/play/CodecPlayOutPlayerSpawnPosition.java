package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutPlayerSpawnPosition;

import com.flowpowered.math.vector.Vector3i;

public final class CodecPlayOutPlayerSpawnPosition implements Codec<MessagePlayOutPlayerSpawnPosition> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutPlayerSpawnPosition message) throws CodecException {
        return context.write(context.byteBufAlloc().buffer(), Vector3i.class, message.getPosition());
    }

    @Override
    public MessagePlayOutPlayerSpawnPosition decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }

}
