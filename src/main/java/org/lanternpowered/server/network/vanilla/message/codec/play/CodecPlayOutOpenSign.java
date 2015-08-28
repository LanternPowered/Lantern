package org.lanternpowered.server.network.vanilla.message.codec.play;

import io.netty.buffer.ByteBuf;
import io.netty.handler.codec.CodecException;

import org.lanternpowered.server.network.message.caching.Caching;
import org.lanternpowered.server.network.message.codec.Codec;
import org.lanternpowered.server.network.message.codec.CodecContext;
import org.lanternpowered.server.network.vanilla.message.type.play.MessagePlayOutOpenSign;

import com.flowpowered.math.vector.Vector3i;

@Caching
public final class CodecPlayOutOpenSign implements Codec<MessagePlayOutOpenSign> {

    @Override
    public ByteBuf encode(CodecContext context, MessagePlayOutOpenSign message) throws CodecException {
        return context.write(context.byteBufAlloc().buffer(), Vector3i.class, message.getPosition());
    }

    @Override
    public MessagePlayOutOpenSign decode(CodecContext context, ByteBuf buf) throws CodecException {
        throw new CodecException();
    }
}
